package org.mengyun.tcctransaction.spring.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = "org.mengyun.tcctransaction", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AnnotationTccTransactionConfiguration.class})})
public class XmlTccTransactionConfiguration {
}
