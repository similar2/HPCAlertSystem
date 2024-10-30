# Prometheus

​	Prometheus是一个监控工具，从ipmi_exporter, node_exporter中收集数据并储存。它自身并不能直接的获取服务器上的性能信息，需要从外界来收集。

<img src="https://prometheus.io/assets/tutorial/architecture.png" alt="img" style="zoom:80%;" />

<img src="C:\Users\14396\AppData\Roaming\Typora\typora-user-images\image-20241011120138480.png" alt="image-20241011120138480" style="zoom:50%;" />

​	它主要功能有：

- 数据库方面

  - 抓取指标（scrape metrics）

    通过HTTP接口抓取各种exporter的指标数据。

    - 配置方法

      首先需要配置ipmi_exporter以及node_exporter, 或者其他exporter。然后在/deploy/prometheus/prometheus.yml，添加对应job。

    - 存储时间序列数据
    
      <img src="C:\Users\14396\AppData\Roaming\Typora\typora-user-images\image-20241011212743372.png" alt="image-20241011212743372" style="zoom:30%;" />
    
      Prometheus会将每条数据都记录timestamp，并且设置label来方便查询以及聚合、分析之类。
    
  - 数据聚合
  
- 查询搜索方面

  - 查询命令
    - 在Prometheus的序列化数据库中，我们可以根据数据的`label`来进行查询。
    - 具体的查询命令在 [官方文档](https://prometheus.io/docs/prometheus/latest/querying/basics/)中可以找到
    - 使用较多的命令有
      - 过滤和匹配label
        - `up{job="node", instance="server1"}`
        - 返回`up`metric中符合条件的数据
        - 这也是我们警报规则的基础
      - 聚合操作
        - `sum() avg() count()`等等
  - 可视化数据
    - Prometheus本身有基本的图表，`http://172.18.6.108:9090/graph`里可以看到不同的折线图
    - 可以通过**Grafana**配置更精细的图表

- 告警系统方面

  - 告警规则
    - 最主要的是根据PromQL查询语句，在每次抓取数据时都会通过报警检查
    - 警告有两种状态，`pending`和`firing`。这和报警规则中的`for`有关。如果定义了这个属性为`5m`,说明如果数据达到了触发警报的阈值，警报只会变成`pending`状态。直到持续5分钟，才会变成`firing`状态，也就是真正出发警报。

### alertmanager

​	这是Prometheus提供的一个警报管理系统，在该项目中没有用到。如果配置了这个管理，可以在http://localhost:9093端口看到。

- 主要功能
  - 警报聚合
    - 比如CPU高温报警，可能CPU一直是高温运行，虽然是同一台机器上的同一个警报，但可能重复很多次。
  - 警报通知
    - alertmanager支持通过邮件，微信，slack等方式通知负责人。
    - 与此同时，可以通过不同的label通知不同的负责人。

### ipmi_exporter

#### 源代码实现

通过BMC服务器，收集硬件信息，例如风扇速度，CPU温度等等。端口在http://localhost:9290

- 配置文件

  - `deploy/docker-compose.yml`

    ```yaml
      ipmi-exporter:
        image: prometheuscommunity/ipmi-exporter:v1.8.0
        container_name: hpc-monitor-ipmi-exporter
        ports:
          - "9290:9290"
        command:
          - --config.file=/ipmi-exporter.yml
        volumes:
          - ./ipmi-exporter.yml:/ipmi-exporter.yml
    ```

  - `deploy/prometheus/prometheus.yml`

    ```yaml
    scrape_configs:
      - job_name: ipmi
        params:
          module: [ 'default' ]
        # scrape_interval: 10s
        metrics_path: /ipmi
        scheme: http
        ...
    ```

  - `deploy/prometheus/targets.yml`

    ```yaml
    - targets: [11.11.3.101, 11.11.3.102, 11.11.3.103, 11.11.3.104]
      labels: {job: ipmi_exporter}
    ```

### node_exporter

收集操作系统层面的信息，例如CPU利用率，内存消耗等等。端口在http://localhost:9100

- 配置文件

  - `deploy/docker-compose.yml`

    ```yaml
      node-exporter:
        image: prom/node-exporter:v1.6.1
        container_name: hpc-monitor-node-exporter
        restart: unless-stopped
        ports:
          - "9100:9100"
        volumes:
          - /proc:/host/proc:ro
          - /sys:/host/sys:ro
          - /:/rootfs:ro
        command:
          - --path.procfs=/host/proc
          - --path.sysfs=/host/sys
          - --collector.filesystem.ignored-mount-points
          - '^/(sys|proc|dev|host|etc)($|/)'
    ```

  - `deploy/prometheus/prometheus.yml`

    ```yaml
      - job_name: node
        scrape_interval: 15s
        static_configs:
          - targets:
              - 172.18.6.108:9100  # IP address of the host where Node Exporter is running
    ```

## 系统实现

### 警告方面

- 获取警告

  - 警告来源

    1. exporter收集原始数据 
    2. Prometheus自动收集原始数据并存储为时间序列数据，收集周期为10s
    3. Prometheus周期性按照告警规则检查是否有警报，周期默认为1min
    4. Prometheus在HTTP端点上展示alert，在本project中是 http://172.18.6.108:9090/api/v1/alerts
    5. (未实现)Prometheus有Alertmanager来帮助管理警报。如果配置了Alertmanager, prometheus在展示警报的同时也会同步给Alertmanager。通过alertmanager，我们可以更方便的实现警报的聚合、静默以及邮件通知之类的功能。
    6. Prometheus.java获取HTTP端点上的警报信息并进一步处理

  - Java实现方法

    1. Prometheus.java调用`getActiveAlerts()`方法发送GET请求，获取json返回值，并将json转化为`PrometheusAlertInfo`对象。抓取的主要方法是对服务器上部署的Prometheus的http接口发送GET请求。返回数据为json，例如

       ~~~json
       {
         "status": "success",
         "data": {
           "alerts": [
             {
               "labels": {
                 "alertname": "HighCPUTemp",
                 "id": "4",
                 "instance": "11.11.3.103",
                 "job": "ipmi_exporter",
                 "name": "CPU0_Temp",
                 "severity": "LOW"
               },
               "annotations": {
                 "summary": "CPU temperature is too high"
               },
               "state": "firing",
               "activeAt": "2024-10-11T03:11:59.608077931Z",
               "value": "5.5e+01"
             },
             ...
           ]
           }
         }
       ~~~
       
	  2. Prometheus.java调用`getAlertFromPrometheus()`定时执行方法，来做到60s获取一次报警信息。同时，使用`jobMappingService`根据警报labels中的job属性来判断警报来源为*ipmi_exporter* 还是 *node_exporter*。由于目前只部署了ipmi_exporter，接下来按照`job == ipmi_exporter`执行
	  
	3. BMCService.java调用`checkAddActiveAlert()`来补全警报信息。根据警报中提供的instance标签，寻找对应的服务器和硬件信息。同时，将这条警报的完整信息传递给`AlertService`
	
	4. AlertService.java将这条警报插入mysql数据库，长时间保存。
	
- 添加或删除告警规则

  - 核心理念是添加/删除规则到`deploy/prometheus/rule.yml`
  
    - 添加规则
  
      - 需要传入`AlertRuleParam`， 生成一条PromQL命令，并且补全Alertname，Duration, Labels等属性，随即写入 `JobService`中存储的`rule.yml`位置，例如
  
        ```json
              - alert: HighCPUTemp
                for: 5m
                annotations: {summary: CPU temperature is too high}
                expr: ipmi_temperature_celsius{job="ipmi_exporter",name=~"CPU[0-9]*_Temp"} > 51
                labels: {severity: LOW}
        
        ```
  
    - 删除规则
  
      - 传入`HardwareType`（BMC，NODE）,`alertname`，然后删除对应的规则
  
  - 在添加规则方面，为了根据不同的硬件类型来获取metric和过滤器选项，在`Bmcservice.java`中做了以下实现
  
    1. `Map<String, Map<String, Map<String, String>>> filterObjects` 
  
      - 这个Hashmap属于`Jobservice`类，存储了<metricName , <filterName, filterExpression\>\>的信息
      - 在初始化时就会读取文件，给这个变量赋值
    
    2. `getMetricNames`
    
      根据提供的`src/main/resources/metric_templates/BMC`文件，得到对于BMC硬件类型可用的筛选metric的名字，例如`ipmi_fan_speed`
    
    3. `getFilterOptionsByMetricName`
    
      - 在选定`metricName`之后，从`filterObject`中获取过滤器选项，例如`SYS_V`
    
    4. `getAlertRuleFilteredExpr`
    
      - 根据传入的`AlertRuleParam`，生成对应的PromQL命令一部分，例如`,name=~"CPU[0-9]*_Temp"`
    
    5. `addAlertRule`
    
      - 最后，将所有组合到一起，形成一个完整的Alert Expression，例如`ipmi_temperature_celsius{job="ipmi_exporter",name=~"CPU[0-9]*_Temp"} > 55`
    
  - 获取现有规则
  
    - 访问http://172.18.6.108:9090/api/v1/rules，并解析json

### 监控对象方面

- 添加硬件对象
  - 填写硬件类型（BMC..），ip地址等信息，传入`edu.sustech.hpc.service.HardwareService`，根据硬件类型，进行下一步处理
  - 添加BMC服务器
    - 传入`HardwareInfo`，写入`prometheus/targets.yml`配置文件
    - 插入数据到`BMC`表
- 

