//package org.mengyun.tcctransaction.sample.dubbo.order.util;
//
//import org.apache.commons.lang3.StringUtils;
//import org.nutz.ioc.Ioc;
//import org.nutz.mvc.Mvcs;
//
//import com.alibaba.dubbo.config.ReferenceConfig;
//import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
//
///**
// * nutz service 工厂类
// * nutz service 工厂类
// * @author [dengxq]
// * @version [版本号, 2016-7-18]
// * @see [相关类/方法]
// * @since [产品/模块版本]*
// */
//public class ServiceFactory {
//    
//	/**
//	 * 获取服务对象
//	 * 
//	 * @param clazz
//	 *            服务对象类型
//	 * @param version
//	 *            服务版本
//	 * @return 服务对象
//	 *  Modify liangcz 2017-03-09 修改reference缓存方式，使用dubbo api提供的 ReferenceConfigCache
//	 */
//	public static final <T> T getService(Class<? extends T> clazz, String version)
//			 {		
//		return getService(clazz,version,-1);
//	}
//	/**
//	 * 获取服务对象
//	 * 
//	 * @param clazz
//	 *            服务对象类型
//	 * @param version
//	 *            服务版本
//	 * @param timeOut
//	 *            超时时间            
//	 * @return 服务对象
//	 *  Modify liangcz 2017-03-09 修改reference缓存方式，使用dubbo api提供的 ReferenceConfigCache
//	 */
//	public static final <T> T getService(Class<? extends T> clazz, String version,int timeOut)
//			 {		
//		Ioc ioc = Mvcs.getIoc();
//		if(ioc == null){
//			ioc = Mvcs.ctx().getDefaultIoc();
//		}
//		ReferenceConfig<T> reference = ioc.get(null, "reference");
//    	reference.setInterface(clazz);
//    	reference.setVersion(version);
//    	if(timeOut != -1){
//    		reference.setTimeout(timeOut);
//    	}
//    	ReferenceConfigCache cache = ReferenceConfigCache.getCache();
//    	// liangcz 2017-03-16 如果引用是空引用，可能服务方在上次调用的时候刚好停机，此时删掉引用，尝试重新获取一次
//    	T t = cache.get(reference);
//    	if(t == null){
//    		cache.destroy(reference);
//    		reference = ioc.get(null, "reference");
//        	reference.setInterface(clazz);
//        	reference.setVersion(version);
//        	if(timeOut != -1){
//        		reference.setTimeout(timeOut);
//        	}
//        	t = cache.get(reference);
//    	}
//    	return t;
//	}
//	/**
//	 * 删除引用缓存
//	 * @param clazz 服务对象类型
//	 * @param version 服务版本
//	 * @param timeOut 超时时间            
//	 * @see ReferenceConfigCache
//	 * @date 2017-03-14
//	 * @author liangcz
//	 */
//	public static void removeCacheService(Class<?> clazz, String version,Integer timeOut){
//		Ioc ioc = Mvcs.getIoc();
//		if(ioc == null){
//			ioc = Mvcs.ctx().getDefaultIoc();
//		}
//		ReferenceConfig<?> reference = ioc.get(null, "reference");
//    	reference.setInterface(clazz);
//    	if(!StringUtils.isBlank(version)){
//    		reference.setVersion(version);
//    	}
//    	if(timeOut != null){
//    		reference.setTimeout(timeOut);
//    	}
//    	ReferenceConfigCache cache = ReferenceConfigCache.getCache();
//    	cache.destroy(reference);
//	}
//	/**
//	 * 获取ioc
//	 * @author liangcz
//	 * @date   2018年8月26日 下午5:22:09
//	 * @return Ioc
//	 */
//	public static Ioc getIoc(){
//		Ioc ioc = Mvcs.getIoc();
//		if(ioc == null){
//			ioc = Mvcs.ctx().getDefaultIoc();
//		}
//		return ioc;
//	}
//}
