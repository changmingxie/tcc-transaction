package org.mengyun.tcctransaction.ha.spring;

import org.mengyun.tcctransaction.Version;
import org.mengyun.tcctransaction.ha.registry.Noop;
import org.mengyun.tcctransaction.ha.registry.Registration;
import org.mengyun.tcctransaction.ha.registry.Registry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.Order;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Lee on 2020/7/27 16:01.
 * aggregate-framework
 */
@Order
public class SpringSmartAutoRegistryService implements SmartLifecycle, InitializingBean {


    private final AtomicBoolean running = new AtomicBoolean(false);

    private Registration registration;
    private Registry registry = new Noop();


    @Override
    public void start() {

        if (running.compareAndSet(false, true)) {
            if (registration != null) {
                registry.register(registration);
            }
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            // 不做解绑
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (registration != null) {
            registration.getMetadata().put("version", Version.getVersion());
        }
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
        runnable.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
