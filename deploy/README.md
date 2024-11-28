# 部署文档

使用 [Docker Compose](https://docs.docker.com/compose) 能够十分方便地部署本项目，步骤如下：

- **部署前注意事项**

  - `Controller` url前缀去除`/api`

    - AlertController.java
      - 改为`@PathController("/alerts")`
    - DeviceController.java
      - 改为`@PathController("/devices")`
    - UserController.java
      - 改为`@PathController("/user")`

  - 规则配置文件路径更改

    - `src/main/java/edu/sustech/hpc/service/JobService.java`
      - 改为`String ruleConfigFile = "/prometheus/rule.yml";`

  - 前端url修改

    - `frontend/src/utils/request.js`
      - 改为`const baseURL = 'http://172.18.6.108/'`  或实际地址 

  - `docker compose.yml`mysql 数据文件位置
  
    - mysql container下`- /home/wuyx/mysql/data:/var/lib/mysql` 删除或改为自定义位置


如果想要本地测试或运行的话，反之即可，也就是加上`/api`前缀、路径位置改为本地位置、url改为`localhost`或本机地址。
     

1. **构建后端**

   进入`/backend`文件夹，使用Maven将后端打成Jar包: `mvn package -D mvn.test.skip=true`。或者使用IDEA自带的Maven插件打Jar包，步骤如图。之后将Jar包移动到`/deploy`文件夹内

    <img src="https://github.com/liqwang/liqwang/assets/90035785/410a2b49-c7b4-4acb-875d-97cca7df0b2c" alt="mvn" style="zoom: 67%;" />

2. **构建前端**

   i. 参考[官方文档](https://nodejs.org/en/learn/getting-started/how-to-install-nodejs)安装Node.js，推荐使用v20.11.0 LTS

   ii. 参考[官方文档](https://pnpm.io/installation#using-corepack)安装pnpm，推荐使用Node.js内置的Corepack安装: `corepack enable pnpm`

   iii. 进入`/frontend`文件夹，运行`pnpm install`安装依赖，然后运行`pnpm build`构建静态文件，将会生成`/dist`文件夹

   iv. 将`/dist`中的文件移动到`/deploy/nginx/html`文件夹内

   ```
   /deploy/nginx
   		 ├─/html
   		 │    ├─favicon.ico
   		 │    ├─index.html
   		 │    └─assets
   		 │        └─...
   		 │    
   		 └─nginx.conf
   ```

3. 将`/deploy`文件夹打包，上传至服务器，并解压`deploy.zip`

4. 在服务器上参考[官方文档](https://docs.docker.com/engine/install/)安装Docker环境

5. 进入`/deploy`文件夹，运行`deploy.sh`脚本即可一键部署
6. 先使用`chmod +x deploy.sh`让脚本可执行，随后`./deploy &`使脚本在后台自动运行
8. 浏览器访问系统前端：http://<ip地址>

