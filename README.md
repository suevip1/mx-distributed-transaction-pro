基于AT模型实现分布式事务解决方案

工具分为两个模块:mx-spring-boot-starter-bt,mx-spring-boot-starter-tx

mx-spring-boot-starter-bt:分支事务端依赖库，担任分布式事务中参与者角色。具体实现包括：拦截Mybatis语句，生成语句镜像并入库持久化存储；开启回滚监听端口，使用tomcat的BIO模型处理回滚连接

mx-spring-boot-starter-tx:分布式事务开启端依赖库，担任分布式事务中协调者角色。具体实现包括：定义自定义注解，业务模块使用该注解即可开启分布式事务；分布式事务处理完成或者异常回滚后与分支事务端建立连接并处理对应操作

mx-distributed-transaction-pro-client:分布式事务发起者，依赖于mx-spring-boot-starter-tx，通过feign接口调用下游服务

mx-distributed-transaction-pro-service0,service1:分布式事务参与者，依赖于mx-spring-boot-starter-bt，承担服务提供角色
