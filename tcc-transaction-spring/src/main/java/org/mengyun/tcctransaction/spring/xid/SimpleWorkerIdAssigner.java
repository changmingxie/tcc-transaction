package org.mengyun.tcctransaction.spring.xid;

import com.xfvape.uid.worker.WorkerIdAssigner;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

public class SimpleWorkerIdAssigner implements WorkerIdAssigner {
    private int maxWorkerId;

    public SimpleWorkerIdAssigner(int workerIdBits) {
        maxWorkerId = ~(-1 << workerIdBits);
    }

    @Override
    public long assignWorkerId() {
        try {
            return generateWorkerIdByMac();
        } catch (Exception e) {
            return generateRandomWorkerId();
        }
    }

    private long generateWorkerIdByMac() throws Exception {
        Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();
        while (all.hasMoreElements()) {
            NetworkInterface networkInterface = all.nextElement();
            boolean isLoopback = networkInterface.isLoopback();
            boolean isVirtual = networkInterface.isVirtual();
            if (isLoopback || isVirtual) {
                continue;
            }
            byte[] mac = networkInterface.getHardwareAddress();
            return ((mac[4] & 0B11) << 8) | (mac[5] & 0xFF);
        }
        throw new RuntimeException("no available mac found");
    }

    /**
     * randomly generate one as workerId
     *
     * @return workerId
     */
    private long generateRandomWorkerId() {
        return new Random().nextInt(maxWorkerId + 1);
    }
}
