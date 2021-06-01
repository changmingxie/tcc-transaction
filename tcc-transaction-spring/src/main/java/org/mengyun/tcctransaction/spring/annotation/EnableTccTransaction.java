package org.mengyun.tcctransaction.spring.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TccTransactionConfigurationSelector.class)
public @interface EnableTccTransaction {
}
