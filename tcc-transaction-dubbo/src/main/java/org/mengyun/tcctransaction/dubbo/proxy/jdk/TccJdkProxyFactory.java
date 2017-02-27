package org.mengyun.tcctransaction.dubbo.proxy.jdk;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import com.alibaba.dubbo.rpc.proxy.jdk.JdkProxyFactory;

import java.lang.reflect.Proxy;

/**
 * Created by changming.xie on 2/26/17.
 */
public class TccJdkProxyFactory extends JdkProxyFactory {

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {

        T proxy = (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));

        T tccProxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new TccInvokerInvocationHandler(proxy,invoker));

        return tccProxy;
    }
}