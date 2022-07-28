package org.mengyun.tcctransaction.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author huabao.fang
 * @Date 2022/7/28 11:33
 * server端流量监控
 * 一期简单处理
 **/
public class ServerFlowMonitor {

    private static Logger logger = LoggerFactory.getLogger(ServerFlowMonitor.class);

    private static AtomicLong totalCounter = new AtomicLong(0);

    // print flow info per 5 min
    private static final int PRINT_GAP_MINUTES = 5;


    static {
        // 开启监控任务
        startMonitorScheduler();
    }

    public static void count(){
        totalCounter.incrementAndGet();
    }


    public static void startMonitorScheduler(){
        Thread t = new Thread("server-flow-monitor-print-thread"){
            private long lastCount = 0;
            private long lastTime = System.currentTimeMillis();

            @Override
            public void run() {
                while (true){
                    try {
                        long currentTime = System.currentTimeMillis();
                        long currentCount = totalCounter.longValue();
                        long secondSpan = (currentTime - lastTime)/1000;
                        long countSpan = currentCount - lastCount;
                        BigDecimal qps = BigDecimal.valueOf(countSpan).divide(new BigDecimal(secondSpan), 2, RoundingMode.HALF_UP);
                        logger.info("current receve qps:{}, total:{}", qps, currentCount);
                        lastCount = currentCount;
                        lastTime = currentTime;
                        Thread.sleep(PRINT_GAP_MINUTES*60*1000);
                    } catch (Exception e) {
                        logger.info("monitor print error", e);
                    }
                }

            }
        };
        t.start();
        t.setDaemon(true);
    }


}
