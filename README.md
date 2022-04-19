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

![image-20220419111437315](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191114719.png)

### 使用Nacos来做服务的注册中心

demo链接：https://github.com/alibaba/spring-cloud-alibaba/blob/2021.x/spring-cloud-alibaba-examples/nacos-example/nacos-discovery-example/readme-zh.md

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191116846.png" alt="image-20220419111640599" style="zoom:50%;" />

1. 首先，修改 pom.xml 文件，引入 Nacos Discovery Starter。

   ```xml
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
   ```

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191120889.png" alt="image-20220419112034523" style="zoom:50%;" />

2. 在应用的 /src/main/resources/application.properties 配置文件中配置 Nacos Server 地址

由于我们还没有Nacos服务，所以我们要去下载。

1. 首先需要获取 Nacos Server，支持直接下载和源码构建两种方式。
    1. 直接下载：[Nacos Server 下载页](https://github.com/alibaba/nacos/releases)
    2. 源码构建：进入 Nacos [Github 项目页面](https://github.com/alibaba/nacos)，将代码 git clone 到本地自行编译打包，[参考此文档](https://nacos.io/zh-cn/docs/quick-start.html)。**推荐使用源码构建方式以获取最新版本**
2. 启动 Server，进入解压后文件夹或编译打包好的文件夹，找到如下相对文件夹 nacos/bin，并对照操作系统实际情况之下如下命令。
    1. Linux/Unix/Mac 操作系统，执行命令 `sh startup.sh -m standalone`
    2. Windows 操作系统，执行命令 `cmd startup.cmd`

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191125323.png" alt="image-20220419112512891" style="zoom:50%;" />

找到这个版本。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191130157.png" alt="image-20220419113041846" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191135244.png" alt="image-20220419113533620" style="zoom:50%;" />

跳转链接：localhost:8848/nacos

登陆账户和密码：nacos	   nacos

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191137756.png" alt="image-20220419113738109" style="zoom:50%;" />

```properties
 spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191140805.png" alt="image-20220419114055475" style="zoom:50%;" />

3. 使用 `@EnableDiscoveryClient` 注解开启服务注册与发现功能

```java
 @SpringBootApplication
 @EnableDiscoveryClient
 public class ProviderApplication {

 	public static void main(String[] args) {
 		SpringApplication.run(ProviderApplication.class, args);
 	}

 	@RestController
 	class EchoController {
 		@GetMapping(value = "/echo/{string}")
 		public String echo(@PathVariable String string) {
 				return string;
 		}
 	}
 }
```

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191143990.png" alt="image-20220419114304858" style="zoom:50%;" />

**应用启动**

1. 增加配置，在 nacos-discovery-provider-example 项目的 /src/main/resources/application.properties 中添加基本配置信息

   ```
    spring.application.name=service-provider
    server.port=18082
   ```

2. 启动应用，支持 IDE 直接启动和编译打包后启动。

    1. IDE直接启动：找到 nacos-discovery-provider-example 项目的主类 `ProviderApplication`，执行 main 方法启动应用。
    2. 打包编译后启动：在 nacos-discovery-provider-example 项目中执行 `mvn clean package` 将工程编译打包，然后执行 `java -jar nacos-discovery-provider-example.jar`启动应用。

![](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191148102.png)

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191241790.png" alt="image-20220419124128531" style="zoom:50%;" />



### 使用Nacos做配置中心

https://github.com/alibaba/spring-cloud-alibaba/blob/2021.x/spring-cloud-alibaba-examples/nacos-example/nacos-config-example/readme-zh.md

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191510568.png" alt="image-20220419150959828" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191512732.png" alt="image-20220419151236009" style="zoom:50%;" />

#### 操作流程

1. 首先，修改 pom.xml 文件，引入 Nacos Config Starter。

   ```xml
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
   ```

2. 在应用的 `/src/main/resources/bootstrap.properties` 配置文件中配置 Nacos Config 元数据

   ```properties
    spring.application.name=nacos-config-example
    spring.cloud.nacos.config.server-addr=127.0.0.1:8848
   ```

3. 测试

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191539914.png" alt="image-20220419153938229" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191540550.png" alt="image-20220419154038784" style="zoom:50%;" />

![image-20220419153830902](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191538797.png)

**假设现在有这样一个需求：我们需要修改 name ，现在项目已经上线了，怎么做**

显然改项目中的配置，是不理智的。我们只需要将我们的配置交给配置中心，我们直接在配置中心改配置文件

我们可以在Nacos的配置中心进行配置。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191925351.png" alt="image-20220419192233525" style="zoom: 33%;" />

如果配置不成功，可以加一下识别bootStrap.properties的依赖。

```xml
<!--针对配置不成功的依赖-->
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```



![image-20220419192445948](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191924835.png)

测试成功。

## 使用Spring Cloud OpenFeign 作远程调用（声明式的HTTP客户端）

https://spring.io/projects/spring-cloud-openfeign

![image-20220419105505687](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191055118.png)

### 简介

也叫粪坑。

Feign 是一个声明式的 HTTP 客户端，它的目的就是让远程调用更加简单。Feign 提供了 HTTP请求的模板，通过编写简单的接口和插入注解，就可以定义好 HTTP 请求的参数、格式、地址等信息。

Feign 整合了 Rilbbon（负载均衡）和Hystrix(服务烯断)，可以让我们不再需要显式地使用这两个组件。

SpringCloudFeign 在 Nettix Feign 的基础上扩展了对 SpringMVC 注解的支持，在其实现下，我们只需创建一个接口并用注解的方式来配置它，即可完成对服务提供方的接口綁定。简化了SpringCloudRibbon 自行封装服务调用客户端的开发量。

### demo

```java
@SpringBootApplication
@EnableFeignClients
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@FeignClient("name")
	static interface NameService {
		@RequestMapping("/")
		public String getName();
	}
}
```

### 案例

需求：member（会员）服务先要远程调用coupon（优惠卷）服务。

### **流程分析**

1. 想要远程调用其他的服务

    1. 引入openfeign的依赖，让他拥有远程调用的能力

    2. 编写一个接口，告诉SpringCloud这个接口需要远程调用远程服务

        1. 声明的接口的每一个方法都是调用那个远程服务的那一个请求

       <img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191456281.png" alt="image-20220419145629061" style="zoom: 33%;" />

    3. 开启远程调用服务的功能 `@EnableFeignClients(basePackages = "com.uin.member.feign")`

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191457431.png" alt="image-20220419145716360" style="zoom: 33%;" />

### 启动测试

会有报错，通过看控制台：No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-loadbalancer?

![image-20220419144940810](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191458077.png)

导入这个负载均衡的依赖。重新测试。

![image-20220419144859227](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191449555.png)







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
