package org.mengyun.tcctransaction.spring.xml;

import org.mengyun.tcctransaction.recovery.RecoverConfiguration;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class TccTransactionByRegisterDefinitionParser implements BeanDefinitionParser {

    public static final String ENABLE_ASPECTJ_AUTO_PROXY_CONFIGURATION = "enableAspectJAutoProxyConfiguration";
    public static final String SPRING_BEAN_FACTORY_BEAN_NAME = "springBeanFactory";
    public static final String CONFIGURABLE_TRANSACTION_ASPECT = "configurableTransactionAspect";
    public static final String CONFIGURABLE_COORDINATOR_ASPECT = "configurableCoordinatorAspect";
    public static final String RECOVERY_CONFIGURATION = "recoveryConfiguration";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        if (!parserContext.getRegistry().containsBeanDefinition(ENABLE_ASPECTJ_AUTO_PROXY_CONFIGURATION)) {
            GenericBeanDefinition enableAspectJAutoProxyConfigurationDef = new GenericBeanDefinition();
            enableAspectJAutoProxyConfigurationDef.setBeanClass(EnableAspectJAutoProxyConfiguration.class);
            parserContext.registerBeanComponent(new BeanComponentDefinition(enableAspectJAutoProxyConfigurationDef, ENABLE_ASPECTJ_AUTO_PROXY_CONFIGURATION));
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

        if (!parserContext.getRegistry().containsBeanDefinition(RECOVERY_CONFIGURATION)) {
            GenericBeanDefinition recoveryConfigurationDef = new GenericBeanDefinition();
            recoveryConfigurationDef.setBeanClass(RecoverConfiguration.class);
            recoveryConfigurationDef.setDependsOn(SPRING_BEAN_FACTORY_BEAN_NAME);

            recoveryConfigurationDef.getPropertyValues().add("transactionRepository", new RuntimeBeanReference(element.getAttribute("transaction-repository")));

            if (element.hasAttribute("recover-frequency")) {
                recoveryConfigurationDef.getPropertyValues().addPropertyValue("recoverFrequency", new RuntimeBeanReference(element.getAttribute("recover-frequency")));
            }

            if (element.hasAttribute("recovery-lock")) {
                recoveryConfigurationDef.getPropertyValues().addPropertyValue("recoveryLock", new RuntimeBeanReference(element.getAttribute("recovery-lock")));
            }

            parserContext.registerBeanComponent(new BeanComponentDefinition(recoveryConfigurationDef, RECOVERY_CONFIGURATION));
        }


        return null;
    }
}
