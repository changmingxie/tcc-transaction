package org.mengyun.tcctransaction.sample.dubbo.capital.service;

import org.junit.Test;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.interceptor.nutz.CompensableTransactionForNutzInterceptor;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.aop.interceptor.LoggingMethodInterceptor;
import org.nutz.aop.matcher.MethodMatcherFactory;

/**
 *
 * @author liangcz
 * @Date   2018年8月26日 下午3:56:33
 * @version 1.0
 */
public class UserAction{ //被AOP的类,必须是public的非abstract类!
    
    /*将要被AOP的方法*/
	@Compensable
    public boolean login(String username, String password) throws Throwable {
        if ("wendal".equals(username) && "qazwsxedc".equals(password)) {
            System.out.println("登陆成功");
            return true;
        }
        System.out.println("登陆失败");
        return false;
    }
    
    public static void main(String[] args) throws Throwable {
        //无AOP的时候
        UserAction ua = new UserAction(); //直接new,将按原本的流程执行
        ua.login("wendal", "qazwsxedc");
        System.out.println("-----------------------------------------------------");
        ClassDefiner cd = DefaultClassDefiner.defaultOne();
        //有AOP的时候
        ClassAgent agent = new AsmClassAgent();
        CompensableTransactionForNutzInterceptor compensableTransactionForNutzInterceptor = new CompensableTransactionForNutzInterceptor();
        agent.addInterceptor(MethodMatcherFactory.matcher("^login$"), compensableTransactionForNutzInterceptor);
        //返回被AOP改造的Class实例
        Class<? extends UserAction> userAction2 = agent.define(cd, UserAction.class);
        UserAction action = userAction2.newInstance();
        action.login("wendal", "qazwsxedc");//通过日志,可以看到方法执行前后有额外的日志
    }
    @Test
    public void test() throws Throwable{
    	//无AOP的时候
        UserAction ua = new UserAction(); //直接new,将按原本的流程执行
        ua.login("wendal", "qazwsxedc");
        System.out.println("-----------------------------------------------------");
        ClassDefiner cd = DefaultClassDefiner.defaultOne();
        //有AOP的时候
        ClassAgent agent = new AsmClassAgent();
        LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor();
        agent.addInterceptor(MethodMatcherFactory.matcher("^login$"), loggingMethodInterceptor);
        //返回被AOP改造的Class实例
        Class<? extends UserAction> userAction2 = agent.define(cd, UserAction.class);
        UserAction action = userAction2.newInstance();
        action.login("wendal", "qazwsxedc");//通过日志,可以看到方法执行前后有额外的日志
    }
}
