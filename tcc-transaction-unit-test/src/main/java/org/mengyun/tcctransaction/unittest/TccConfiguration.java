package org.mengyun.tcctransaction.unittest;

import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTccTransaction
public class TccConfiguration {
}
