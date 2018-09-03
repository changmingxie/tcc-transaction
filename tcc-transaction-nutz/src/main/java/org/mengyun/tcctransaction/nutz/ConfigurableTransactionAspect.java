package org.mengyun.tcctransaction.nutz;
//package org.mengyun.tcctransaction.spring;
//
//import org.mengyun.tcctransaction.TransactionManager;
//import org.mengyun.tcctransaction.interceptor.CompensableTransactionAspect;
//import org.mengyun.tcctransaction.interceptor.CompensableTransactionInterceptor;
//import org.mengyun.tcctransaction.support.TransactionConfigurator;
///**
// * Created by changmingxie on 10/30/15.
// */
//public class ConfigurableTransactionAspect extends CompensableTransactionAspect {
//
//    private TransactionConfigurator transactionConfigurator;
//
//    public void init() {
//
//        TransactionManager transactionManager = transactionConfigurator.getTransactionManager();
//
//        CompensableTransactionInterceptor compensableTransactionInterceptor = new CompensableTransactionInterceptor();
//        compensableTransactionInterceptor.setTransactionManager(transactionManager);
//        compensableTransactionInterceptor.setDelayCancelExceptions(transactionConfigurator.getRecoverConfig().getDelayCancelExceptions());
//
//        this.setCompensableTransactionInterceptor(compensableTransactionInterceptor);
//    }
//
//    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
//        this.transactionConfigurator = transactionConfigurator;
//    }
//}
