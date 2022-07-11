package org.mengyun.tcctransaction.server.controller;

import org.mengyun.tcctransaction.TccServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("recover")
public class RecoveryController {

    @Autowired
    private TccServer tccServer;

    @RequestMapping("/start/{domain}")
    @ResponseBody
    public String startRecover(@PathVariable("domain") String domain) {
        tccServer.getTransactionStoreRecovery().startRecover(domain);
        return "triggered";
    }
}
