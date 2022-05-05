# Mall商城(基础篇的开发)

## 项目简介

Mall商城项目致力于打造一个完整的大型分布式架构的电商平台，采用先阶段流行的前后端分离模式编写。

Mall商城是一套电商项目，包括前台的商城系统以及后台管理系统，基于SpringCloud、SpringCloud Alibaba、Mybatis-plus实现。

前台商城系统包括：用户登陆、注册、商品搜索、商品详情、购物车、订单、秒杀活动等模块。

后台管理系统包括：系统管理、商品系统、优惠营销、库存系统、订单系统、用户系统、内容管理等七大模块。

## 项目架构

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021433733.png" alt="image-20220502143323434" style="zoom:50%;" />

![image-20220502143436881](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021434272.png)

## 接口文档

https://easydoc.net/s/78237135/ZUqEdvA4/d3EezLdO

## 项目演示

### 前台商城系统项目演示

### 后台管理系统项目演示

## 电商项目的基础

### SPU和SKU

**SPU**：`Standerd Product  Unit`（标准化产品单元）是商品信息聚合的最小单位，是一组可复用、易检索的标准化信息的集合，该集合描述了一个产品的特性。

**SKU**：`Stock keeping  Unit`（库存单位），即库存进出的计量单位，可以是件、盒、托盘等为单位。SKU 这是对于大型连锁超市DC（配送中戏）物流管理的一个必要的方法。现在已经被引申为统一编号的简称，每中产品均对应有唯一的SKU号。

例如：iPhone XS MAX是SPU，IPhone XS MAX 128G 黑色是SKU.

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204221744259.png" alt="image-20220422174404535" style="zoom:50%;" />

### 规格参数【基本参数】和销售属性

每个分类下的商品共享规格参数，与销售属性。

只是有些商品不一定要用这个分类下全部的属性。

- 属性是以三级分类组织起来的
- 规格参数重有些是可以提供检索的
- 规格参数也是基本属性，他们具有自己的分组
- 属性的分组也是以三级分类组织起来的
- 属性名确定的，但是值是每一个商品不同来决定的

SPU来决定规格参数的值，SKU来决定销售属性的值。

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

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191930491.png" alt="image-20220419193011854" style="zoom:50%;" />

我们可以在Nacos的配置中心进行配置。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191925351.png" alt="image-20220419192233525" style="zoom: 33%;" />

**如果配置中心和当前项的配置文件都配置了相同的项，优先使用配置中心的配置。**

**如果配置不成功，可以加一下识别bootStrap.properties的依赖。**

```xml
<!--针对配置不成功的依赖-->
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

![image-20220419192445948](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191924835.png)

测试成功。

#### 更多的细节

##### 命名空间

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191937464.png" alt="image-20220419193748602" style="zoom:50%;" />

用于进行租户粒度的**配置隔离**。不同的命名空间下，可以存相同的 Group 或 Data ID的配置。Namespace 的常用场景之一是不同的环境的配置的区分隔离，例如开发测试环境和生产环境（如配置、服务）隔离等。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191940413.png" alt="image-20220419194007136" style="zoom:50%;" />

![image-20220419194450139](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191944536.png)

![image-20220419194541509](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191945748.png)

利用命名空间做环境隔离。

**基于微服务之间互相隔离配置，每一个微服务都创建自己的命名空间，只加载自己的命名空间。**

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191948318.png" alt="image-20220419194829355" style="zoom:50%;" />

##### 配置集

一组相关或者不想关的配置项的集合称为配置集。本系统中，一个配置文件通常就是一个配置集，包含了系统各个方面的配置。例如，一个配置集可能包含了数据源、线程池、日志级别的配置项。

##### 配置集ID

类似于文件名。

![image-20220419195323816](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191953156.png)

##### 配置分组

默认所有的配置集都属于：DEFAULT_GROUP

比如叫双11、618、双12.

```properties
#更改默认组
spring.cloud.nacos.config.group=1111
```

**在本项目中采用每个微服务创建自己的命名空间，使用配置分组区分环境，比如dev、test、prod环境**。

**怎么去同时加载多个配置文件？**

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204192012755.png" alt="image-20220419201222925" style="zoom:50%;" />

```properties
spring.application.name=coupon
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
spring.cloud.nacos.config.namespace=f00c6f6d-ccad-4762-8599-f09604d2915f
spring.cloud.nacos.config.group=prod

#---------------相当于加载了一个datasource.yml配置文件----tart-----
spring.cloud.nacos.config.extension-configs[0].data-id=datasource.yml
#那个组 dev
spring.cloud.nacos.config.extension-configs[0].group=dev
#动态刷新
spring.cloud.nacos.config.extension-configs[0].refresh=true
#---------------相当于加载了一个配置文件----end-----

#---------------相当于加载了一个mybatis.yml配置文件----tart-----
spring.cloud.nacos.config.extension-configs[1].data-id=mybatis.yml
#那个组 dev
spring.cloud.nacos.config.extension-configs[1].group=dev
#动态刷新
spring.cloud.nacos.config.extension-configs[1].refresh=true
#---------------相当于加载了一个配置文件----end-----

#---------------相当于加载了一个other.yml配置文件----tart-----
spring.cloud.nacos.config.extension-configs[2].data-id=other.yml
#那个组 dev
spring.cloud.nacos.config.extension-configs[2].group=dev
#动态刷新
spring.cloud.nacos.config.extension-configs[2].refresh=true
#---------------相当于加载了一个配置文件----end-----
```

当然是注释掉了，application.yml 中的配置文件。

![](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204192026244.png)

![image-20220419202723264](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204192027668.png)

![image-20220419202812563](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204192028392.png)

##### 总结

**我们以后给微服务配置任何信息，都可以在配置中心去配置，只需要在 bootstrap.properties 中声明加载那些配置文件。**

**配置中心有的优先使用配置中心的。**

### 关于nacos的更多的信息

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204192033520.png" alt="image-20220419203353721" style="zoom:50%;" />

https://nacos.io/zh-cn/docs/what-is-nacos.html

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

### 操作流程

1. 完善对应的版本信息
2. 要使用网关，需要开起API网关的服务与发现，这样请求可以通过我们的网关，网关可以发现其他的服务，并将请求转发到其他的服务。

https://spring.io/guides/gs/gateway/

https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/



## 使用Spring Cloud Sleuth 来做调用链的监控

https://spring.io/projects/spring-cloud-sleuth

![image-20220419105208463](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191052608.png)

## 使用Spring Cloud Alibaba Seata来做分布式事务的解决方案

https://github.com/seata/seata

![image-20220419105326003](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191104519.png)

![image-20220419105643264](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204191056740.png)

## 使用Spring Cloud Alibaba-OSS来做文件上传

**上传方案选择：服务端签名后直传**

![image-20220421201402398](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204212014493.png)

上传之前先找我们的服务器，要一个防伪签名，拿着防伪签名，去访问oss。

[Java (aliyun.com)](https://help.aliyun.com/document_detail/32007.html)

[服务端签名后直传 (aliyun.com)](https://help.aliyun.com/document_detail/31926.html)

本文主要介绍如何基于Post Policy的使用规则在服务端通过各种语言代码完成签名，然后通过表单直传数据到OSS。由于服务端签名直传无需将AccessKey暴露在前端页面，相比JavaScript客户端签名直传具有更高的安全性。

![image-20220422081540921](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204220815033.png)

[Java (aliyun.com)](https://help.aliyun.com/document_detail/91868.htm?spm=a2c4g.11186623.0.0.16075d3fNC6Cvu#concept-ahk-rfz-2fb)

### 应用服务器核心代码解析

应用服务器源码包含了签名直传服务以及上传回调服务的完整示例代码。

**注意** 以下仅提供核心代码片段，如需了解这两个功能的完整实现，请参见[应用服务器源码（Java版本）](https://gosspublic.alicdn.com/doc/91868/aliyun-oss-appserver-java-master.zip)。

- 签名直传服务

  签名直传服务响应客户端发送给应用服务器的GET消息，代码片段如下：

  ```java
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
  
          // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
         String accessId = "yourAccessKeyId";      
         String accessKey = "yourAccessKeySecret"; 
         // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
         String endpoint = "oss-cn-hangzhou.aliyuncs.com"; 
         // 填写Bucket名称，例如examplebucket。
         String bucket = "examplebucket"; 
         // 填写Host名称，格式为https://bucketname.endpoint。                   
         String host = "https://examplebucket.oss-cn-hangzhou.aliyuncs.com"; 
         // 设置上传回调URL，即回调服务器地址，用于处理应用服务器与OSS之间的通信。OSS会在文件上传完成后，把文件上传信息通过此回调URL发送给应用服务器。
         String callbackUrl = "https://192.168.0.0:8888";
         // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
         String dir = "exampledir/"; 
  
          // 创建OSSClient实例。
          OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
          try {
              long expireTime = 30;
              long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
              Date expiration = new Date(expireEndTime);
              // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
              PolicyConditions policyConds = new PolicyConditions();
              policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
              policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
  
              String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
              byte[] binaryData = postPolicy.getBytes("utf-8");
              String encodedPolicy = BinaryUtil.toBase64String(binaryData);
              String postSignature = ossClient.calculatePostSignature(postPolicy);
  
              Map<String, String> respMap = new LinkedHashMap<String, String>();
              respMap.put("accessid", accessId);
              respMap.put("policy", encodedPolicy);
              respMap.put("signature", postSignature);
              respMap.put("dir", dir);
              respMap.put("host", host);
              respMap.put("expire", String.valueOf(expireEndTime / 1000));
              // respMap.put("expire", formatISO8601Date(expiration));
  
              JSONObject jasonCallback = new JSONObject();
              jasonCallback.put("callbackUrl", callbackUrl);
              jasonCallback.put("callbackBody",
                      "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
              jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
              String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
              respMap.put("callback", base64CallbackBody);
  
              JSONObject ja1 = JSONObject.fromObject(respMap);
              // System.out.println(ja1.toString());
              response.setHeader("Access-Control-Allow-Origin", "*");
              response.setHeader("Access-Control-Allow-Methods", "GET, POST");
              response(request, response, ja1.toString());
  
          } catch (Exception e) {
              // Assert.fail(e.getMessage());
              System.out.println(e.getMessage());
          } finally { 
              ossClient.shutdown();
          }
      }
  ```

- 上传回调服务

  上传回调服务响应OSS发送给应用服务器的POST消息，代码片段如下：

  ```java
  protected boolean VerifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody)
              throws NumberFormatException, IOException {
          boolean ret = false;
          String autorizationInput = new String(request.getHeader("Authorization"));
          String pubKeyInput = request.getHeader("x-oss-pub-key-url");
          byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
          byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
          String pubKeyAddr = new String(pubKey);
          if (!pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")
                  && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
              System.out.println("pub key addr must be oss addrss");
              return false;
          }
          String retString = executeGet(pubKeyAddr);
          retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
          retString = retString.replace("-----END PUBLIC KEY-----", "");
          String queryString = request.getQueryString();
          String uri = request.getRequestURI();
          String decodeUri = java.net.URLDecoder.decode(uri, "UTF-8");
          String authStr = decodeUri;
          if (queryString != null && !queryString.equals("")) {
              authStr += "?" + queryString;
          }
          authStr += "\n" + ossCallbackBody;
          ret = doCheck(authStr, authorization, retString);
          return ret;
      }
  
      protected void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
          String ossCallbackBody = GetPostBody(request.getInputStream(),
                  Integer.parseInt(request.getHeader("content-length")));
          boolean ret = VerifyOSSCallbackRequest(request, ossCallbackBody);
          System.out.println("verify result : " + ret);
          // System.out.println("OSS Callback Body:" + ossCallbackBody);
          if (ret) {
              response(request, response, "{\"Status\":\"OK\"}", HttpServletResponse.SC_OK);
          } else {
              response(request, response, "{\"Status\":\"verify not ok\"}", HttpServletResponse.SC_BAD_REQUEST);
          }
      }
  ```

  关于上传回调的API接口说明，请参见[Callback](https://help.aliyun.com/document_detail/31989.htm#section-btz-phx-wdb)。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204220857241.png" alt="image-20220422085720181" style="zoom:50%;" />

## 使用前端自定义校验和后端JSR303做双重校验

前端自定义校验实现可参考前端代码，这里只介绍后端的校验的实现。

后端校验只需要在需要校验的字段加上相关的校验规则，并告诉SpringMVC需要校验的参数。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204221411635.png" alt="image-20220422141150944" style="zoom:50%;" />

同时可以自定义错误提示。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204221417288.png" alt="image-20220422141732455" style="zoom:50%;" />

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204221412951.png" alt="image-20220422141217831" style="zoom:50%;" />

## 使用SpringMVC的@ControllerAdvice实现统一异常的处理

```java
package com.uin.product.exception;

import com.uin.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglufei
 * @description: 集中处理（感知）错误
 * @date 2022/4/22/2:43 PM
 */
@Slf4j
//@ControllerAdvice(basePackages = "com.uin.product.app")
//@ResponseBody
//效果一样
@RestControllerAdvice(basePackages = "com.uin.product.app")
public class UinControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerFormNumException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题：{},异常类型：{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item) -> {
            //错误消息提示
            String message = item.getDefaultMessage();
            //那个字段出现了问题
            String field = item.getField();
            //将这些错误的信息用map装起来
            map.put(field, message);
        });
        return R.error().put("data", map);
    }

}
```

## 搭建服务过程中遇到的问题

此处之描述大问题，小问题一百度就出来了。

### 当使用gateway网关将后端的请求路由到后端的renren-fast服务是出现图片不显示,服务出现503或者404

同时高版本的还需要添加:

```xml
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-loadbalancer</artifactId>
</dependency>
```

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204202110119.png" alt="image-20220420210741759" style="zoom:50%;" />

![image-20220420211005170](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202204202110217.png)

## 总结

历经11天，终于写完了商场的基础分布式项目的搭建和维护。中途也奔溃过、难受过、放弃过。每次特别难受的时候，跑到厕所偷偷的哭会儿。每天早8晚9，待在实验室硬肝这个项目，逃过很多课，所以还在肝的兄弟们，别放弃！相信自己！好吧？

中间还要出去找暑假实习，去面试。终于在4.27号，找到了一个自己还算是满意的公司，给的薪资也可以。先将就干着吧，毕竟也是第一次实习。

<img src="https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021413377.png" alt="image-20220502141351418" style="zoom:50%;" />

还记得在4.26的面试，面试官还问到我，服务之间的远程调用。我吊打面试官！！！面完就给offer，不过实习给的工资有点低，公司还可以，做银行的业务。

沿途拍到的。

![image-20220502141512277](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021415801.png)

![image-20220502141606431](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021416887.png)

![image-20220502141627481](https://bearbrick0.oss-cn-qingdao.aliyuncs.com/images/img/202205021416914.png)

收获很多，特别感谢雷丰阳老师。收获很多。不行了，眼泪快掉下来了。😭

在此总结分布式基础篇做了哪些内容。

- 分布式的基础篇概念

微服务、注册中心、配置中心、远程调用、Feign、网关

- 基础开发

SpringBoot 2.6.3、SpringCloud、Mybatis-Plus、Vue组件化、阿里云对象存储

- 环境

Ubuntu 20.0.4、Docker、Mysql、Redis、逆向工程&人人开源

- 开发规范

1. 数据校验JSR303、全局异常处理、全局统一返回、全局跨域处理
2. 枚举状态、业务状态码、VO与TO、PO的划分、逻辑删除
3. Lombok：@Data、@Slf4j

