package org.mengyun.tcctransaction.ha.spring;

import org.mengyun.tcctransaction.ha.registry.Owner;
import org.mengyun.tcctransaction.ha.registry.Registration;
import org.mengyun.tcctransaction.ha.registry.RegistrationUtil;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 2020/9/28 10:36.
 * tcc-transaction
 */
public class RegistrationSpringFactory implements FactoryBean<Registration>, InitializingBean {

    private TransactionRepository transactionRepository;
    private String application;
    private List<Owner> owners;
    private Map<String, Object> meta = new HashMap<>();
    private Registration registration;

    @Override
    public Registration getObject() throws Exception {
        return registration;
    }

    @Override
    public Class<?> getObjectType() {
        return registration == null ? Registration.class : registration.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Registration registration = RegistrationUtil.get(transactionRepository);
        registration.setApplication(application);
        registration.setOwners(owners);
        if (meta != null && !meta.isEmpty()) {
            registration.getMetadata().putAll(meta);
        }
        this.registration = registration;
    }
}
