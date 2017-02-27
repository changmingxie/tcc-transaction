package org.mengyun.tcctransaction.support;

/**
 * Created by changmingxie on 11/20/15.
 */
public interface BeanFactory {
    <T> T getBean(Class<T> var1);

    <T> boolean isFactoryOf(Class<T> clazz);
}
