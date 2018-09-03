package org.mengyun.tcctransaction.nutz.support;

import java.util.concurrent.ConcurrentHashMap;

import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.mengyun.tcctransaction.nutz.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.support.BeanFactory;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * nutz bean 工厂
 * @author  liangcz
 * @Date    2018年8月13日 下午5:21:48
 * @version 1.0
 */
@IocBean
public class NutzBeanFactory implements BeanFactory {
	private ConcurrentHashMap<Class<?>,Object> map;
    @Override
    public boolean isFactoryOf(Class clazz) {
    	Object obj = getBean(clazz);
        return obj == null ? false : true;
    }

    @Override
    public <T> T getBean(Class<T> var1) {
        T t = IocBeanUtil.getBean(var1);
        if(t != null){
    		return t;
    	}
        t = getSpecialBean(var1);
        if(t != null){
    		return t;
    	}
        if(t == null && var1.isInterface()){
        	try {
            	t = ServiceFactory.getService(var1, null);
    		} catch (Exception e) {
    		}
        }
        return t;
    }
    /**
     * 自定义bean
     * @author liangcz
     * @date   2018年8月30日 下午6:10:35
     * @return T
     */
    public <T> T getSpecialBean(Class<T> clazz){
    	if(map == null){
    		map = new ConcurrentHashMap<Class<?>, Object>();
    		ResourceCoordinatorAspect resourceCoordinatorAspect = (ResourceCoordinatorAspect) IocBeanUtil.getBean(null, "configurableCoordinatorAspectForNutz");
    		resourceCoordinatorAspect.setResourceCoordinatorInterceptor((ResourceCoordinatorInterceptor) IocBeanUtil.getBean(null, "resourceCoordinatorInterceptor"));
    		map.put(ResourceCoordinatorAspect.class, resourceCoordinatorAspect);
    		map.put(DefaultRecoverConfig.class, (DefaultRecoverConfig) IocBeanUtil.getBean(null, "recoverConfig"));
    	}
    /*	IocBeanUtil.getBean("configurableCoordinatorAspectForNutz")
    	Mvcs.ctx().getDefaultIoc().get(null, "configurableCoordinatorAspectForNutz")*/
    	T t = (T) map.get(clazz);
    	return t;
    } 
    
    
}
