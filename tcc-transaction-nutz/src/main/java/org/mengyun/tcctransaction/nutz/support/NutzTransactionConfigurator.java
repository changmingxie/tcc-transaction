package org.mengyun.tcctransaction.nutz.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.nutz.interceptor.CompensableTransactionForNutzInterceptor;
import org.mengyun.tcctransaction.nutz.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.recover.RecoverConfig;
import org.mengyun.tcctransaction.repository.CachableTransactionRepository;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;

/**
 * Created by changmingxie on 11/11/15.
 */
@IocBean(create="init")
public class NutzTransactionConfigurator implements TransactionConfigurator {

    private static volatile ExecutorService executorService = null;

    @Inject
    private TransactionRepository transactionRepository;

    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;

    @Inject
    private TransactionManager transactionManager;
    
    @Inject
    private NutzBeanFactory nutzBeanFactory;

    public void init() {
    	
    	if(nutzBeanFactory.isFactoryOf(DefaultRecoverConfig.class)){
    		recoverConfig = nutzBeanFactory.getBean(DefaultRecoverConfig.class);
    	}
    	CompensableTransactionForNutzInterceptor compensableTransactionForNutzInterceptor = nutzBeanFactory.getBean(CompensableTransactionForNutzInterceptor.class);
    	compensableTransactionForNutzInterceptor.setDelayCancelExceptions(recoverConfig.getDelayCancelExceptions());
    	if(transactionManager == null){
    		transactionManager = nutzBeanFactory.getBean(TransactionManager.class);
    	}
        if(transactionRepository == null){
        	transactionRepository = nutzBeanFactory.getBean(TransactionRepository.class);
        }
        transactionManager.setTransactionRepository(transactionRepository);

        if (executorService == null) {

            synchronized (NutzTransactionConfigurator.class) {

                if (executorService == null) {
//                    executorService = new ThreadPoolExecutor(recoverConfig.getAsyncTerminateThreadPoolSize(),
//                            recoverConfig.getAsyncTerminateThreadPoolSize(),
//                            0L, TimeUnit.SECONDS,
//                            new SynchronousQueue<Runnable>());
                    executorService = Executors.newCachedThreadPool();
                }
            }
        }

        transactionManager.setExecutorService(executorService);

        if (transactionRepository instanceof CachableTransactionRepository) {
            ((CachableTransactionRepository) transactionRepository).setExpireDuration(recoverConfig.getRecoverDuration());
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
