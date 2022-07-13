
# TCC-TRANSACTION
## TCC-TRANSACTION是什么
[TCC-TRANSACTION](https://changmingxie.github.io)是一款开源的微服务架构下的TCC型分布式事务解决方案，致力于提供高性能和简单易用的分布式事务服务。
- Try: 尝试执行业务，完成所有业务检查（一致性），预留必须业务资源（准隔离性） 
- Confirm: 确认执行业务，不作任何业务检查，只使用Try阶段预留的业务资源，满足幂等性
- Cancel: 取消执行业务，释放Try阶段预留的业务资源，满足幂等性

![TCC调用流程](material/tcc-invoke.webp)  

## 功能列表
### TCC管理后台
- 登录
- Domain管理
- 事件管理
- 任务管理

### TCC-SERVER
- 事件补偿操作
- 事件存储控制

### 支持事件存储方式
#### server模式
- remoting

#### local模式
- redis
- shard-redis
- redis-cluster
- jdbc
- rocksdb
- memory

### 支持RPC框架
- dubbo 
- openfegin
- grpc

## 快速开始

[快速开始](https://changmingxie.github.io/zh-cn/docs/tutorial/quickstart.html)


## 常见问题
[常见问题](https://changmingxie.github.io/zh-cn/docs/faq.html)

## 讨论群

钉钉扫码入群

![钉钉扫码入群](https://raw.githubusercontent.com/changmingxie/tcc-transaction/master-1.6.x/material/tcc-transaction-dingdingtalk.jpg)
