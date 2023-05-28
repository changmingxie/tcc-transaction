package org.mengyun.tcctransaction.spring.xml;

import com.xfvape.uid.impl.CachedUidGenerator;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.SpringTccClient;
import org.mengyun.tcctransaction.spring.configuration.XmlTccTransactionConfiguration;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.xid.DefaultUUIDGenerator;
import org.mengyun.tcctransaction.spring.xid.SimpleWorkerIdAssigner;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class TccTransactionByRegisterDefinitionParser implements BeanDefinitionParser {

    public static final String XML_TCC_TRANSACTION_CONFIGURATION = "xmlTccTransactionConfiguration";

    public static final String SPRING_BEAN_FACTORY_BEAN_NAME = "springBeanFactory";

    public static final String CONFIGURABLE_TRANSACTION_ASPECT = "configurableTransactionAspect";

    public static final String CONFIGURABLE_COORDINATOR_ASPECT = "configurableCoordinatorAspect";

    public static final String UUID_GENERATOR = "uuidGenerator";

    public static final String CACHED_UID_GENERATOR = "cachedUidGenerator";

    public static final String TCC_CLIENT = "tccClient";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(XML_TCC_TRANSACTION_CONFIGURATION)) {
            GenericBeanDefinition xmlTccTransactionConfigurationDef = new GenericBeanDefinition();
            xmlTccTransactionConfigurationDef.setBeanClass(XmlTccTransactionConfiguration.class);
            parserContext.registerBeanComponent(new BeanComponentDefinition(xmlTccTransactionConfigurationDef, XML_TCC_TRANSACTION_CONFIGURATION));
        }
        if (!parserContext.getRegistry().containsBeanDefinition(SPRING_BEAN_FACTORY_BEAN_NAME)) {
            GenericBeanDefinition springBeanFactoryDef = new GenericBeanDefinition();
            springBeanFactoryDef.setBeanClass(SpringBeanFactory.class);
            parserContext.registerBeanComponent(new BeanComponentDefinition(springBeanFactoryDef, SPRING_BEAN_FACTORY_BEAN_NAME));
        }
        if (!parserContext.getRegistry().containsBeanDefinition(CONFIGURABLE_TRANSACTION_ASPECT)) {
            GenericBeanDefinition transactionAspectDef = new GenericBeanDefinition();
            transactionAspectDef.setBeanClass(ConfigurableTransactionAspect.class);
            parserContext.registerBeanComponent(new BeanComponentDefinition(transactionAspectDef, CONFIGURABLE_TRANSACTION_ASPECT));
        }
        if (!parserContext.getRegistry().containsBeanDefinition(CONFIGURABLE_COORDINATOR_ASPECT)) {
            GenericBeanDefinition coordinationAspectDef = new GenericBeanDefinition();
            coordinationAspectDef.setBeanClass(ConfigurableCoordinatorAspect.class);
            parserContext.registerBeanComponent(new BeanComponentDefinition(coordinationAspectDef, CONFIGURABLE_COORDINATOR_ASPECT));
        }
        if (!parserContext.getRegistry().containsBeanDefinition(UUID_GENERATOR)) {
            int timeBits = 28;
            int workBits = 22;
            int seqBits = 13;
            GenericBeanDefinition cachedUidGeneratorDef = new GenericBeanDefinition();
            cachedUidGeneratorDef.setBeanClass(CachedUidGenerator.class);
            MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
            mutablePropertyValues.addPropertyValue("epochStr", "2022-01-01");
            mutablePropertyValues.addPropertyValue("timeBits", timeBits);
            mutablePropertyValues.addPropertyValue("workerBits", workBits);
            mutablePropertyValues.addPropertyValue("seqBits", seqBits);
            mutablePropertyValues.addPropertyValue("workerIdAssigner", new SimpleWorkerIdAssigner(workBits));
            cachedUidGeneratorDef.setPropertyValues(mutablePropertyValues);
            parserContext.registerBeanComponent(new BeanComponentDefinition(cachedUidGeneratorDef, CACHED_UID_GENERATOR));
            GenericBeanDefinition uuidGeneratorDef = new GenericBeanDefinition();
            uuidGeneratorDef.setBeanClass(DefaultUUIDGenerator.class);
            uuidGeneratorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(CACHED_UID_GENERATOR));
            parserContext.registerBeanComponent(new BeanComponentDefinition(uuidGeneratorDef, UUID_GENERATOR));
        }
        if (!parserContext.getRegistry().containsBeanDefinition(TCC_CLIENT)) {
            GenericBeanDefinition tccClientDef = new GenericBeanDefinition();
            tccClientDef.setBeanClass(SpringTccClient.class);
            tccClientDef.setDependsOn(SPRING_BEAN_FACTORY_BEAN_NAME);
            if (element.hasAttribute("client-config")) {
                tccClientDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(element.getAttribute("client-config")));
            } else {
                tccClientDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new ConstructorArgumentValues.ValueHolder(null));
            }
            parserContext.registerBeanComponent(new BeanComponentDefinition(tccClientDef, TCC_CLIENT));
        }
        return null;
    }
}
