# 部署文档

使用 [Docker Compose](https://docs.docker.com/compose) 能够十分方便地部署本项目，步骤如下：

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

5. 进入`/deploy`文件夹，运行`docker compose up -d`即可一键部署

   启动prometheus容器时可能会遇到如下权限不足的报错，导致无法启动

   ```
   caller=query_logger.go:87 level=error component=activeQueryTracker msg="Error opening query log file" file=/prometheus/queries.active err="open /prometheus/queries.active: permission denied"
   panic: Unable to create mmap-ed active query log
   ```

   参考https://github.com/prometheus/prometheus/issues/9704，赋权`chmod 777 ./prometheus/data`即可解决

6. 浏览器访问系统前端：http://<ip地址>

