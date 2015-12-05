package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by changmingxie on 11/22/15.
 */
@Component
public class TccApplicationContext implements BeanFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getBean(Class<?> aClass) {
        return this.applicationContext.getBean(aClass);
    }
}
