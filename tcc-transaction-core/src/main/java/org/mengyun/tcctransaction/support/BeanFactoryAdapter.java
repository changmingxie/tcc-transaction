package org.mengyun.tcctransaction.support;

/**
 * Created by changmingxie on 11/20/15.
 */
public class BeanFactoryAdapter {

    private static BeanFactory beanFactory;

    public static Object getBean(Class<?> aClass) {
        return beanFactory.getBean(aClass);
    }

    public static void setBeanFactory(BeanFactory beanFactory) {
        BeanFactoryAdapter.beanFactory = beanFactory;
    }
}
