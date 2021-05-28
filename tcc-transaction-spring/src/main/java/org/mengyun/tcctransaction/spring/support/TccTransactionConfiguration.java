package org.mengyun.tcctransaction.spring.support;


import org.mengyun.tcctransaction.recovery.RecoverConfiguration;
import org.mengyun.tcctransaction.recovery.RecoverFrequency;
import org.mengyun.tcctransaction.recovery.RecoveryLock;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.xml.EnableAspectJAutoProxyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EnableAspectJAutoProxyConfiguration.class)
public class TccTransactionConfiguration {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired(required = false)
    RecoverFrequency recoverFrequency;

    @Autowired(required = false)
    RecoveryLock recoveryLock;

    @Bean("springBeanFactory")
    public SpringBeanFactory getSpringBeanFactory() {
        return new SpringBeanFactory();
    }

    @Bean
    public ConfigurableTransactionAspect getConfigurableTransactionAspect() {
        return new ConfigurableTransactionAspect();
    }

    @Bean
    public ConfigurableCoordinatorAspect getConfigurableCoordinatorAspect() {
        return new ConfigurableCoordinatorAspect();
    }

    @Bean
    @DependsOn("springBeanFactory")
    public RecoverConfiguration getRecoverConfiguration() {
        RecoverConfiguration recoverConfiguration = new RecoverConfiguration();
        recoverConfiguration.setTransactionRepository(transactionRepository);

        if (recoverFrequency != null) {
            recoverConfiguration.setRecoverFrequency(recoverFrequency);
        }

        if (recoveryLock != null) {
            recoverConfiguration.setRecoveryLock(recoveryLock);
        }

        return recoverConfiguration;
    }
}
