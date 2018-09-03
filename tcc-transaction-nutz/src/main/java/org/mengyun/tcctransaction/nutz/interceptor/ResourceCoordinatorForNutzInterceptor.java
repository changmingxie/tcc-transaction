package org.mengyun.tcctransaction.nutz.interceptor;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.InvocationContext;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.nutz.support.NutzSupportUtils;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.ReflectionUtils;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 资源拦截器
 * @author  liangcz
 * @Date    2018年8月31日 上午11:00:35
 * @version 1.0
 */
@IocBean
public class ResourceCoordinatorForNutzInterceptor implements MethodInterceptor{
	@Inject
    private TransactionManager transactionManager;
	static final Logger logger = Logger.getLogger(ResourceCoordinatorForNutzInterceptor.class.getSimpleName());

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    @Override
	public void filter(InterceptorChain chain) throws Throwable {
    	Method method = NutzSupportUtils.getCompensableMethod(chain);
    	if(method == null){
    		chain.doChain();
    		return;
    	}
    	Transaction transaction = transactionManager.getCurrentTransaction();
    	String callMethodName = chain.getCallingObj().getClass().getSimpleName() + "." + NutzSupportUtils.getCompensableMethod(chain).getName() + "()";
    	// logger.info("[callMethodName]:" + callMethodName + ",[transaction]:" + (transaction == null ? "null" : transaction.getStatus()));
    	
        if (transaction != null) {

            switch (transaction.getStatus()) {
                case TRYING:
                    enlistParticipant(chain);
                    break;
                case CONFIRMING:
                    break;
                case CANCELLING:
                    break;
            }
        }
        chain.doChain();
	}


    private void enlistParticipant(InterceptorChain chain) throws IllegalAccessException, InstantiationException {

        Method method = NutzSupportUtils.getCompensableMethod(chain);
        if (method == null) {
            throw new RuntimeException("没有定义Compensable注解");
        }
        Compensable compensable = method.getAnnotation(Compensable.class);

        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();

        Transaction transaction = transactionManager.getCurrentTransaction();
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        if (FactoryBuilder.factoryOf(compensable.transactionContextEditor()).getInstance().get(chain.getCallingObj(), method, chain.getArgs()) == null) {
            FactoryBuilder.factoryOf(compensable.transactionContextEditor()).getInstance().set(new TransactionContext(xid, TransactionStatus.TRYING.getId()), chain.getCallingObj(), chain.getCallingMethod(), chain.getArgs());
        }
        
        
        Class targetClass = ReflectionUtils.getDeclaringType(chain.getCallingMethod().getDeclaringClass(), method.getName(), method.getParameterTypes());

        // Class targetClass = chain.getCallingMethod().getDeclaringClass();
        InvocationContext confirmInvocation = new InvocationContext(targetClass,
                confirmMethodName,
                method.getParameterTypes(), chain.getArgs());

        InvocationContext cancelInvocation = new InvocationContext(targetClass,
                cancelMethodName,
                method.getParameterTypes(), chain.getArgs());

        Participant participant =
                new Participant(
                        xid,
                        confirmInvocation,
                        cancelInvocation,
                        compensable.transactionContextEditor());

        transactionManager.enlistParticipant(participant);

    }

	


}
