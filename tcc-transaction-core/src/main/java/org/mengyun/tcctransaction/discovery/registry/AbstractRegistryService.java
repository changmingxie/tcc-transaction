package org.mengyun.tcctransaction.discovery.registry;

import org.mengyun.tcctransaction.exception.RegistryException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Nervose.Wu
 * @date 2022/5/17 11:09
 */
public abstract class AbstractRegistryService implements RegistryService {

    private String clusterName;

    private volatile List<String> serverAddresses = new ArrayList<>();

    @Override
    public void start() {
        //do nothing by default
    }

    @Override
    public void register(InetSocketAddress address) {
        try {
            doRegister(address);
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void subscribe() {
        try {
            doSubscribe();
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public List<String> lookup() {
        return serverAddresses;
    }

    @Override
    public void close() {
        //do nothing by default
    }

    protected abstract void doRegister(InetSocketAddress address) throws Exception;

    protected abstract void doSubscribe() throws Exception;

    protected void setServerAddresses(List<String> serverAddresses) {
        Collections.shuffle(serverAddresses, ThreadLocalRandom.current());
        this.serverAddresses = serverAddresses;
    }

    protected String getClusterName() {
        return clusterName;
    }

    protected void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
