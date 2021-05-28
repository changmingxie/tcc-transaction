package org.mengyun.tcctransaction.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class TccConfigNamespaceHandlerSupport extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("annotation-driven", new TccTransactionByRegisterDefinitionParser());
    }
}
