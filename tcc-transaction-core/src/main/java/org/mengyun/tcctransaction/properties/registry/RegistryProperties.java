package org.mengyun.tcctransaction.properties.registry;

import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;
import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.mengyun.tcctransaction.discovery.registry.direct.DirectRegistryProperties;
import org.mengyun.tcctransaction.discovery.registry.nacos.NacosRegistryProperties;
import org.mengyun.tcctransaction.discovery.registry.zookeeper.ZookeeperRegistryProperties;

/**
 * @author Nervose.Wu
 * @date 2022/5/23 17:58
 */
public class RegistryProperties implements RegistryConfig {

    private String clusterName = "default";

    private RegistryType registryType = RegistryType.direct;

    private String customRegistryName;

    private ZookeeperRegistryProperties zookeeper = new ZookeeperRegistryProperties();

    private NacosRegistryProperties nacos = new NacosRegistryProperties();

    private DirectRegistryProperties direct = new DirectRegistryProperties();

    @Override
    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public RegistryType getRegistryType() {
        return registryType;
    }

    public void setRegistryType(RegistryType registryType) {
        this.registryType = registryType;
    }

    @Override
    public String getCustomRegistryName() {
        return customRegistryName;
    }

    public void setCustomRegistryName(String customRegistryName) {
        this.customRegistryName = customRegistryName;
    }

    @Override
    public ZookeeperRegistryProperties getZookeeperRegistryProperties() {
        return zookeeper;
    }

    public void setZookeeper(ZookeeperRegistryProperties zookeeper) {
        this.zookeeper = zookeeper;
    }

    @Override
    public NacosRegistryProperties getNacosRegistryProperties() {
        return nacos;
    }

    public void setNacos(NacosRegistryProperties nacos) {
        this.nacos = nacos;
    }

    @Override
    public DirectRegistryProperties getDirectRegistryProperties() {
        return direct;
    }

    public void setDirect(DirectRegistryProperties direct) {
        this.direct = direct;
    }

}
