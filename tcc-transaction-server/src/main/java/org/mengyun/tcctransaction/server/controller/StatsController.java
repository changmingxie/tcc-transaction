package org.mengyun.tcctransaction.server.controller;

import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.stats.StatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nervose.Wu
 * @date 2024/1/19 17:10
 */
@RestController
public class StatsController {

    @Autowired
    private TccServer tccServer;

    @RequestMapping("/server/stats")
    public StatsDto metrics() {
        if (tccServer.getStatsManager() == null) {
            return null;
        }
        return tccServer.getStatsManager().getStats();
    }
}
