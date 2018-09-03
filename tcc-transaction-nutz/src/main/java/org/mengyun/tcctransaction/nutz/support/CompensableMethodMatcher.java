package org.mengyun.tcctransaction.nutz.support;

import java.lang.reflect.Method;

import org.mengyun.tcctransaction.api.Compensable;
import org.nutz.aop.MethodMatcher;

/**
 * tcc 可补充方法匹配器
 * @author liangcz
 * @Date   2018年8月26日 下午4:34:33
 * @version 1.0
 */
public class CompensableMethodMatcher implements MethodMatcher{
	/**
	 * 匹配有@compensable注解的方法
	 */
	@Override
	public boolean match(Method method) {
		Compensable compensable = method.getAnnotation(Compensable.class);
		if(compensable != null){
			return true;
		}
		return false;
	}
	
}
