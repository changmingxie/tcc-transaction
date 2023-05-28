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
 */
public class ServerFlowMonitor {

    private static Logger logger = LoggerFactory.getLogger(ServerFlowMonitor.class);

    private static AtomicLong totalCounter = new AtomicLong(0);

    private ServerFlowMonitor() {
    }

    public static void count() {
        totalCounter.incrementAndGet();
    }

    // 开启监控任务
    public static void startMonitorScheduler(int flowMonitorPrintIntervalMinutes) {
        Thread t = new Thread("server-flow-monitor-thread") {

            private long lastCount = 0;

            private long lastTime = -1;

            @Override
            public void run() {
                while (true) {
                    try {
                        long currentTime = System.currentTimeMillis();
                        long currentCount = totalCounter.longValue();
                        if (lastTime != -1) {
                            // 第一次跳过
                            long secondSpan = (currentTime - lastTime) / 1000;
                            long countSpan = currentCount - lastCount;
                            if (countSpan != 0) {
                                // 没有流量时跳过
                                BigDecimal qps = BigDecimal.valueOf(countSpan).divide(new BigDecimal(secondSpan), 2, RoundingMode.HALF_UP);
                                logger.info("flow monitor report, request-qps:{} calculated in {} min, request-total:{}", qps, flowMonitorPrintIntervalMinutes, currentCount);
                            }
                        }
                        lastCount = currentCount;
                        lastTime = currentTime;
                    } catch (Exception e) {
                        logger.info("monitor print error", e);
                    } finally {
                        try {
                            Thread.sleep(flowMonitorPrintIntervalMinutes * 60L * 1000);
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
        logger.info("server flow monitor thread started");
    }
}
