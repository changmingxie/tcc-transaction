package org.mengyun.tcctransaction.spring;

import org.mengyun.tcctransaction.ClientConfig;
import org.mengyun.tcctransaction.TccClient;

public class SpringTccClient extends TccClient implements TransactionManagerFactory {

    public SpringTccClient() {
        super(new ClientConfig());
    }

    public SpringTccClient(ClientConfig clientConfig) {
        super(clientConfig);
    }
}
