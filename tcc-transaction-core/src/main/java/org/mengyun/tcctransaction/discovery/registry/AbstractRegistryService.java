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

    private volatile List<String> serverAddressesForDashboard = new ArrayList<>();

    @Override
    public void start() {
        //do nothing by default
    }

    @Override
    public void register(InetSocketAddress address, InetSocketAddress addressForDashboard) {
        try {
            doRegister(address, addressForDashboard);
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void subscribe(boolean addressForDashboard) {
        try {
            doSubscribe(addressForDashboard);
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public List<String> lookup(boolean addressForDashboard) {
        return addressForDashboard ? serverAddressesForDashboard : serverAddresses;
    }

    @Override
    public void close() {
        //do nothing by default
    }

    protected abstract void doRegister(InetSocketAddress address, InetSocketAddress addressForDashboard) throws Exception;

    protected abstract void doSubscribe(boolean addressForDashboard) throws Exception;

    protected void setServerAddresses(List<String> address, boolean addressForDashboard) {
        Collections.shuffle(serverAddresses, ThreadLocalRandom.current());
        if (addressForDashboard) {
            this.serverAddressesForDashboard = address;
        } else {
            this.serverAddresses = address;
        }
    }

    protected String getClusterName() {
        return clusterName;
    }

    protected void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
