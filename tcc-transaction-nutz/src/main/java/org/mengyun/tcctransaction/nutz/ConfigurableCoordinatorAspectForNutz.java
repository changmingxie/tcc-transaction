package org.mengyun.tcctransaction.nutz;

import org.aspectj.lang.annotation.Aspect;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by changmingxie on 11/8/15.
 */
@IocBean
@Aspect
public class ConfigurableCoordinatorAspectForNutz extends ResourceCoordinatorAspect{
	@Inject
    private TransactionConfigurator transactionConfigurator;

    @Override
    public int getOrder() {
        return 0;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
