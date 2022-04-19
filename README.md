# Mall商城

## 项目简介

正在补充。。。



## 模块化分

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180832801.png" alt="image-20220418083220804" style="zoom:50%;" />

后续的分布式模块正补充....

## 环境搭建

本地环境：

````bash
root@uin-virtual-machine:/# uname -a
Linux uin-virtual-machine 5.11.0-38-generic #42~20.04.1-Ubuntu SMP Tue Sep 28 16:01:15 UTC 2021 aarch64 aarch64 aarch64 GNU/Linux
root@uin-virtual-machine:/# 
````

增加一个小tip：配置静态IP，配了都说好。



![image-20220417225156497](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204182013911.png)

![image-20220417223147088](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204182013604.png)

Gateway 网关地址 10.211.55.1、Iface网卡 eth0、netmask子网掩码 255.255.255.0、当前IP 10.211.55.15

[为Ubuntu 20.04 设置静态IP简明教程（和把大象装冰箱一样简单） - 云+社区 - 腾讯云 (tencent.com)](https://cloud.tencent.com/developer/article/1933335)

### docker

dokcer在mac本地安装是很简单的事情，比如用homebrew。

ubuntu安装docker。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180816948.png" alt="image-20220417144636865" style="zoom:50%;" />

[ubuntu 18.04 arm64版 安装docker 踩坑_踩坑的小方的博客-CSDN博客_arm ubuntu安装docker](https://blog.csdn.net/qiangrenpu8881/article/details/104863690)

在此感谢博主！！。

![image-20220417150136634](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180817260.png)

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

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180817404.png" alt="image-20220417153916157" style="zoom:50%;" />

### docker安装Mysql

由于我是Arm的系统架构，Docker Hub上没有适用于arm64架构的mysql镜像。

```bash
docker pull mysql/mysql-server:latest
```

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180817695.png" alt="image-20220417134052004" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180817628.png" alt="image-20220417134449733" style="zoom:50%;" />

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

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180817262.png" alt="image-20220417135534346" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818660.png" alt="image-20220417140901713" style="zoom:50%;" />

**Ubuntu docker安装Mysql**

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818428.png" alt="image-20220417150703705" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818231.png" alt="image-20220417150942898" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818346.png" alt="image-20220417151127579" style="zoom:50%;" />

## 项目搭建

### 版本说明

[版本说明 · alibaba/spring-cloud-alibaba Wiki (github.com)](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

SpringClound Alibaba 组件的版本。

![image-20220417180959128](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818277.png)

SpringCloud的版本。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204180818564.png" alt="image-20220417181119503" style="zoom:50%;" />

## 使用快速开发平台renren-fast

### 搭建后台系统

```bash
git clone https://gitee.com/renrenio/renren-fast.git
```

### 搭建后台的前端页面

```bash
git clone https://gitee.com/renrenio/renren-fast-vue.git
```

## 各模块的端口规定

商品服务   product      8081

仓储服务   ware   		8082

订单服务 	order 		8083

优惠券服务	coupon 	8084

会员服务 	member		8085

## 搭建分布式的基本环境

**架构图**

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191012364.png" alt="image-20220419101222812" style="zoom:50%;" />

## 技术选型

![image-20220419102214121](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191025754.png)

其他的组件详情：[微服务相关组件 - BeaBrick0 - 博客园 (cnblogs.com)](https://www.cnblogs.com/bearbrick0/p/15781897.html)

## 版本选择

https://github.com/alibaba/spring-cloud-alibaba

https://github.com/alibaba/spring-cloud-alibaba/blob/2021.x/README-zh.md

![image-20220419102434215](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191024255.png)

![image-20220419102450019](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191024385.png)

## 使用Spring cloud Alibaba Nacos做配置中心和注册中心

![image-20220419105411070](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191054042.png)

Nacos 注册中心（服务发现/注册）

Nacos 配置中心（动态配置管理）

https://github.com/alibaba/Nacos

![image-20220419105940889](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191059158.png)

之前我们使用Spring Cloud Eurek 来做，但是后来停止维护了。

## 使用Spring Cloud OpenFeign 作远程调用（声明式的HTTP客户端）

https://spring.io/projects/spring-cloud-openfeign

![image-20220419105505687](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191055118.png)

## 使用Spring Cloud Alibaba Sentinel 服务容错（限流、熔断、降级）

之前使用的Spring Cloud Netflix  Hystrix来做服务降级、服务熔断

https://github.com/alibaba/Sentinel

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191058294.png" alt="image-20220419105741226" style="zoom:50%;" />

## 使用Spring Cloud Gateway 做ApI网关（webFlux编程）

详细配置介绍：https://spring.io/projects/spring-cloud-gateway

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191048158.png" alt="image-20220419104848527" style="zoom:50%;" />



## 使用Spring Cloud Sleuth 来做调用链的监控

https://spring.io/projects/spring-cloud-sleuth

![image-20220419105208463](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191052608.png)

## 使用Spring Cloud Alibaba Seata来做分布式事务的解决方案

https://github.com/seata/seata

![image-20220419105326003](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191104519.png)

![image-20220419105643264](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191056740.png)

