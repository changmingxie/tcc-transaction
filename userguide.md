
配置tcc-transaction

在项目中需要引用tcc-transaction-spring jar包，如使用maven依赖：
        <dependency>
            <groupId>org.mengyun</groupId>
            <artifactId>tcc-transaction-spring</artifactId>
            <version>${project.version}</version>
        </dependency>

另外，启动应用时，需要将tcc-transaction-spring jar中的tcc-transaction.xml加入到classpath中。如在web.xml中配置：
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:tcc-transaction.xml
        </param-value>
    </context-param>

如何发布Tcc服务

发布一个Tcc服务方法，可被远程调用并参与到Tcc事务中，发布Tcc服务方法有下面四个约束：
1. 在服务方法上加上@Compensable注解
2. 服务方法第一个入参类型为org.mengyun.tcctransaction.api.TransactionContext
3. 服务方法的入参都须能序列化(实现Serializable接口)
4. try方法、confirm方法和cancel方法入参类型须一样

tcc-transaction将拦截加上了@Compensable注解的服务方法，并根据Compensalbe的confirmMethod和cancelMethod获取在CONFRIM阶段和CANCEL阶段需要调用的方法。
tcc-transaction在调用confirmMethod或是cancelMethod时是根据发布Tcc服务的接口类在Spring的ApplicationContext中获取Tcc服务实例，并调用confirmMethod或cancelMethod指定方法。
因此如果是使用动态代理的方式实现aop(默认方式）,则confirmMethod和cancelMethod需在接口类中声明，如果使用动态字节码技术实现aop（如指定aspectj-autoproxy的proxy-target-class属性为true),则无需在接口类中声明。

服务方法第一个org.mengyun.tcctransaction.api.TransactionContext类型的入参为预留入参，tcc-transaction使用此参数将在远程调用时传递TCC事务上下文信息。
tcc-transaction在执行服务过程中会将Tcc服务的上下文持久化，包括所有入参，内部实现为将入参使用jdk自带的序列化机制序列化为为byte流，所以需要实现Serializable接口。

在tcc-transaction-dubbo-capital中发布Tcc服务示例：

try方法：
@Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    public void record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

confirm方法：
public void confirmRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

cancel方法：
public void cancelRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

在tcc-transaction-dubbo-redpacket中发布Tcc服务示例：

try方法：
@Compensable(confirmMethod = "confirmRecord",cancelMethod = "cancelRecord")
    public void record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {

confirm方法：
public void confirmRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {

cancel方法：
public void cancelRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {

如何调用远程Tcc服务
调用远程Tcc服务，将远程Tcc服务参与到本地Tcc事务中，本地的服务方法也需要声明为Tcc服务，与发布一个Tcc服务不同，本地Tcc服务方法有三个约束：
1. 在服务方法上加上@Compensable注解
2. 服务方法的入参都须能序列化(实现Serializable接口)
3. try方法、confirm方法和cancel方法入参类型须一样

即与发布Tcc服务不同的是本地Tcc服务无需声明服务方法第一个入参类型为org.mengyun.tcctransaction.api.TransactionContext。

在tcc-transaction-dubbo-order中调用远程Tcc服务示例：

try方法：
@Compensable(confirmMethod = "confirmMakePayment",cancelMethod = "cancelMakePayment")
    public void makePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {

confirm方法：
public void confirmMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {

cancel方法：
public void cancelMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {





