# tcc-transaction
tcc-transaction是TCC型事务java实现

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

