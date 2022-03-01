package org.tcctransaction.sample.multiple.tier.trade;

import org.mengyun.tcctransaction.repository.MemoryStoreTransactionRepository;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableTccTransaction
@Configuration
public class AppConfig {

    @Bean
    public TransactionRepository getTransactionRepository() {

        MemoryStoreTransactionRepository transactionRepository = new MemoryStoreTransactionRepository();
        transactionRepository.setDomain("trade");
        return transactionRepository;
    }

}
