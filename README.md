

Try: 尝试执行业务

    完成所有业务检查（一致性）

    预留必须业务资源（准隔离性）

Confirm: 确认执行业务

    真正执行业务

    不作任何业务检查

    只使用Try阶段预留的业务资源

    Confirm操作满足幂等性

Cancel: 取消执行业务

    释放Try阶段预留的业务资源

    Cancel操作满足幂等性


示例说明:

tcc-transaction-tutorial-sample示例演示了在使用dubbo作为rpc调用情况下如何使用tcc-transaction(tcc-transaction不和底层使用的rpc框架耦合，也就是使用doubbo,thrift,web service,http等都可)。

本示例演示在下完订单后,使用红包帐户和资金帐户来付款，红包帐户服务和资金帐户服务在不同的系统中。示例中，有两个SOA提供方，一个是CapitalTradeOrderService，代表着资金帐户服务,另一个是RedPacketTradeOrderService,代表着红包帐户服务。

下完订单后，订单状态为DRAFT，在TCC事务中TRY阶段，订单支付服务将订单状态变成PAYING，同时远程调用红包帐户服务和资金帐户服务,将付款方的余额减掉（预留业务资源);如果在TRY阶段，任何一个服务失败，tcc-transaction将自动调用这些服务对应的cancel方法，订单支付服务将订单状态变成PAY_FAILED,同时远程调用红包帐户服务和资金帐户服务,将付款方余额减掉的部分增加回去；如果TRY阶段正常完成，则进入CONFIRM阶段，在CONFIRM阶段（tcc-transaction自动调用）,订单支付服务将订单状态变成CONFIRMED,同时远程调用红包帐户服务和资金帐户服务对应的CONFIRM方法，将收款方的余额增加。特别说明下，由于是示例，在CONFIRM和CANCEL方法中没有实现幂等性，如果在真实项目中使用，需要保证CONFIRM和CANCEL方法的幂等性。

在运行sample前，需搭建好db环境，运行dbscripts目录下的create_db.sql建立数据库实例及表；还需修改各种项目中jdbc.properties文件中的jdbc连接信息。

使用指南：https://github.com/changmingxie/tcc-transaction/wiki/%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97
如有问题可以在本项目的github issues中提问。
