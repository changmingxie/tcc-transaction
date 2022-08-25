package org.mengyun.tcctransaction.spring.configuration;


import com.xfvape.uid.UidGenerator;
import com.xfvape.uid.impl.CachedUidGenerator;
import org.mengyun.tcctransaction.ClientConfig;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.SpringTccClient;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.xid.DefaultUUIDGenerator;
import org.mengyun.tcctransaction.spring.xid.SimpleWorkerIdAssigner;
import org.mengyun.tcctransaction.xid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = "org.mengyun.tcctransaction", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {XmlTccTransactionConfiguration.class})})
public class AnnotationTccTransactionConfiguration {

    @Autowired(required = false)
    private ClientConfig clientConfig;

    @Bean("springBeanFactory")
    public SpringBeanFactory getSpringBeanFactory() {
        return new SpringBeanFactory();
    }


    @Bean("configurableTransactionAspect")
    public ConfigurableTransactionAspect getConfigurableTransactionAspect() {
        return new ConfigurableTransactionAspect();
    }

    @Bean("configurableCoordinatorAspect")
    public ConfigurableCoordinatorAspect getConfigurableCoordinatorAspect() {
        return new ConfigurableCoordinatorAspect();
    }

    @Bean
    @DependsOn({"springBeanFactory"})
    public SpringTccClient getTccClient() {
        return new SpringTccClient(clientConfig);
    }

    @Bean
    public UidGenerator uidGenerator() {
        int timeBits = 28;
        int workBits = 22;
        int seqBits = 13;
        CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
        cachedUidGenerator.setEpochStr("2022-01-01");
        cachedUidGenerator.setWorkerBits(workBits);
        cachedUidGenerator.setTimeBits(timeBits);
        cachedUidGenerator.setSeqBits(seqBits);
        cachedUidGenerator.setWorkerIdAssigner(new SimpleWorkerIdAssigner(workBits));
        return cachedUidGenerator;
    }

    @Bean
    public UUIDGenerator uuidGenerator() {
        return new DefaultUUIDGenerator(uidGenerator());
    }
}
