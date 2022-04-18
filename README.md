# U-mall商城

正在补充。。

## 项目简介

正在补充。。



## 环境搭建

本地环境：

````bash
root@uin-virtual-machine:/# uname -a
Linux uin-virtual-machine 5.11.0-38-generic #42~20.04.1-Ubuntu SMP Tue Sep 28 16:01:15 UTC 2021 aarch64 aarch64 aarch64 GNU/Linux
root@uin-virtual-machine:/# 
````

### docker

dokcer在mac本地安装是很简单的事情，比如用homebrew。

ubuntu安装docker。

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417144636865.png" alt="image-20220417144636865" style="zoom:50%;" />

[ubuntu 18.04 arm64版 安装docker 踩坑_踩坑的小方的博客-CSDN博客_arm ubuntu安装docker](https://blog.csdn.net/qiangrenpu8881/article/details/104863690)

在此感谢博主！！。

![image-20220417150136634](/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417150136634.png)

[ubuntu中设置docker以及容器开机自启 - 代码先锋网 (codeleading.com)](https://www.codeleading.com/article/58746255343/)

这一步很重，可以省去很多不必要的麻烦。

### docker安装Redis（并开启持久化）

```bash
docker pull redis
```

```bash
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf
```

```bash
docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data -v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf -d redis redis-server /etc/redis/redis.conf
```

[使用docker安装redis并持久化_马克图布No1的博客-CSDN博客_docker redis 持久化](https://blog.csdn.net/weixin_40271376/article/details/108810375)

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417153916157.png" alt="image-20220417153916157" style="zoom:50%;" />

### docker安装Mysql

由于我是Arm的系统架构，Docker Hub上没有适用于arm64架构的mysql镜像。

```bash
docker pull mysql/mysql-server:latest
```

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417134052004.png" alt="image-20220417134052004" style="zoom:50%;" />

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417134449733.png" alt="image-20220417134449733" style="zoom:50%;" />

**创建并启动MySQL服务容器**

```bash
docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -d mysql/mysql-server
```

这时使用宿主机连接没有授权访问，需要进入mysql修改mysql访问权限。

```bash
docker exec -it mysql bash
```

```bash
mysql -u root -p 123456
```

```sql
#授权
CREATE USER 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL ON *.* TO 'root'@'%';
```

```sql
#刷新权限

mysql> flush privileges;
```

```sql
修改root用户密码

mysql> ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```

```sql
#刷新权限

mysql> flush privileges;
```

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417135534346.png" alt="image-20220417135534346" style="zoom:50%;" />

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417140901713.png" alt="image-20220417140901713" style="zoom:50%;" />

**Ubuntu docker安装Mysql**

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417150703705.png" alt="image-20220417150703705" style="zoom:50%;" />

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417150942898.png" alt="image-20220417150942898" style="zoom:50%;" />

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417151127579.png" alt="image-20220417151127579" style="zoom:50%;" />

## 项目搭建

### 版本说明

[版本说明 · alibaba/spring-cloud-alibaba Wiki (github.com)](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

SpringClound Alibaba 组件的版本。

![image-20220417180959128](/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417180959128.png)

SpringCloud的版本。

<img src="/Users/wanglufei/Library/Application Support/typora-user-images/image-20220417181119503.png" alt="image-20220417181119503" style="zoom:50%;" />

## 使用快速开发平台renren-fast

### 搭建后台系统

```bash
git clone https://gitee.com/renrenio/renren-fast.git
```

### 搭建后台的前端页面

```bash
git clone https://gitee.com/renrenio/renren-fast-vue.git
```





