package org.mengyun.tcctransaction.nutz.interceptor;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.NoExistedTransactionException;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.MethodType;
import org.mengyun.tcctransaction.nutz.support.NutzSupportUtils;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;
import org.mengyun.tcctransaction.utils.ReflectionUtils;
import org.mengyun.tcctransaction.utils.TransactionUtils;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.alibaba.fastjson.JSON;

/**
 * 可补偿拦截器
 * @author  liangcz
 * @Date    2018年8月31日 上午11:01:11
 * @version 1.0
 */
@IocBean
public class CompensableTransactionForNutzInterceptor implements MethodInterceptor {

    static final Logger logger = Logger.getLogger(CompensableTransactionForNutzInterceptor.class.getSimpleName());
    @Inject
    private TransactionManager transactionManager;
    
    private Set<Class<? extends Exception>> delayCancelExceptions;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions = delayCancelExceptions;
    }
    
    @Override
	public void filter(InterceptorChain chain) throws Throwable {
    	Method method = NutzSupportUtils.getCompensableMethod(chain);
    	if(method == null){
    		chain.doChain();
    		return;
    	}
        Compensable compensable = method.getAnnotation(Compensable.class);
        Propagation propagation = compensable.propagation();
        TransactionContext transactionContext = FactoryBuilder.factoryOf(compensable.transactionContextEditor()).getInstance().get(chain.getCallingObj(), method, chain.getArgs());

        boolean asyncConfirm = compensable.asyncConfirm();

        boolean asyncCancel = compensable.asyncCancel();

        boolean isTransactionActive = transactionManager.isTransactionActive();

        if (!TransactionUtils.isLegalTransactionContext(isTransactionActive, propagation, transactionContext)) {
            throw new SystemException("no active compensable transaction while propagation is mandatory for method " + method.getName());
        }

        MethodType methodType = CompensableMethodUtils.calculateMethodType(propagation, isTransactionActive, transactionContext);
        String callMethodName = chain.getCallingObj().getClass().getSimpleName() + "." + method.getName() + "()";
        // logger.info("[callMethod]:" + callMethodName + ",[isTransactionActive]:" + isTransactionActive + ",[methodType]:" + methodType + "transactionContext: " + (transactionContext == null ? "null" : "not null"));
        if(org.mengyun.tcctransaction.common.MethodType.ROOT.equals(methodType)){
        	rootMethodProceed(chain, asyncConfirm, asyncCancel);
        }else  if(org.mengyun.tcctransaction.common.MethodType.PROVIDER.equals(methodType)){
        	providerMethodProceed(chain, transactionContext, asyncConfirm, asyncCancel);
        }else{
        	chain.doChain();
        }
      /* switch (methodType) {
            case ROOT:
                rootMethodProceed(chain, asyncConfirm, asyncCancel);
            case PROVIDER:
                providerMethodProceed(chain, transactionContext, asyncConfirm, asyncCancel);
            default:
                chain.doChain();
        }*/
	}


    private Object rootMethodProceed(InterceptorChain chain, boolean asyncConfirm, boolean asyncCancel) throws Throwable {

        Object returnValue = null;

        Transaction transaction = null;

        try {

            transaction = transactionManager.begin();

            try {
            	chain.doChain();
                returnValue = chain.getReturn();
            } catch (Throwable tryingException) {

                if (isDelayCancelException(tryingException)) {
                    transactionManager.syncTransaction();
                } else {
                    logger.warn(String.format("compensable transaction trying failed. transaction content:%s", JSON.toJSONString(transaction)), tryingException);

                    transactionManager.rollback(asyncCancel);
                }

                throw tryingException;
            }

            transactionManager.commit(asyncConfirm);

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        return returnValue;
    }

    private Object providerMethodProceed(InterceptorChain chain, TransactionContext transactionContext, boolean asyncConfirm, boolean asyncCancel) throws Throwable {

        Transaction transaction = null;
        try {

            switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
                case TRYING:
                    transaction = transactionManager.propagationNewBegin(transactionContext);
                    chain.doChain();
                    return chain.getReturn();
                case CONFIRMING:
                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.commit(asyncConfirm);
                    } catch (NoExistedTransactionException excepton) {
                        //the transaction has been commit,ignore it.
                    }
                    break;
                case CANCELLING:

                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.rollback(asyncCancel);
                    } catch (NoExistedTransactionException exception) {
                        //the transaction has been rollback,ignore it.
                    }
                    break;
            }

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        Method method = chain.getCallingMethod();

        return ReflectionUtils.getNullValue(method.getReturnType());
    }

    private boolean isDelayCancelException(Throwable throwable) throws ClassNotFoundException {

        if (delayCancelExceptions != null) {
            for (Object delayCancelStr : delayCancelExceptions) {
            	Class<?> delayCancelException = null;
            	if (delayCancelStr instanceof Class) {
            		delayCancelException = (Class) delayCancelStr;
				}else{
					delayCancelException = Class.forName((String) delayCancelStr);
				}
                Throwable rootCause = ExceptionUtils.getRootCause(throwable);

                if (delayCancelException.isAssignableFrom(throwable.getClass())
                        || (rootCause != null && delayCancelException.isAssignableFrom(rootCause.getClass()))) {
                    return true;
                }
            }
        }

        return false;
    }

	

}
