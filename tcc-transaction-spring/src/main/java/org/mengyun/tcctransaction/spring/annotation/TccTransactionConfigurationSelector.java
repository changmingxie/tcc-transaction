package org.mengyun.tcctransaction.spring.annotation;

import org.mengyun.tcctransaction.spring.configuration.AnnotationTccTransactionConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class TccTransactionConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] { AnnotationTccTransactionConfiguration.class.getName() };
    }
}
