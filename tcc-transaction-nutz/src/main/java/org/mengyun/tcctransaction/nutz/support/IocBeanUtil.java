package org.mengyun.tcctransaction.nutz.support;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

/**
* nutz 框架bean获取工具类
* @author     liangcz
* @version    版本号 1.0.0, 2017-03-28 
* @see        
* @since      
 */
public class IocBeanUtil {
	/**
	 * 获取对应的bean
	 * @param clazz 类型
	 * @param name bean名字
	 * @date 2017-03-28
	 * @author liangcz
	 * @return T
	 */
	public static <T> T getBean(Class<T> clazz,String name){
		try {
			Ioc ioc = Mvcs.getIoc();
			if(ioc == null){
				ioc = Mvcs.ctx().getDefaultIoc();
			}
			T t = null;
			if(Strings.isBlank(name)){
				t = ioc.get(clazz);
			}else{
				t = ioc.get(clazz, name);
			}
			return t;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 获取对应的bean
	 * @param clazz 类型
	 * @param name bean名字
	 * @date 2017-03-28
	 * @author liangcz
	 * @return T
	 */
	public static <T> T getBean(String name){
		return getBean(null,name);
	}
	/**
	 * 获取对应的bean
	 * @param clazz 类型
	 * @date 2017-03-28
	 * @author liangcz
	 * @return T
	 */
	public static <T> T getBean(Class<T> clazz){
		return getBean(clazz,null);
	}
	
}
