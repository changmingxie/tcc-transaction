package org.mengyun.tcctransaction.ha.registry;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.mengyun.tcctransaction.ha.registry.jdbc.JDBCRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisClusterRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisShardedRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 注册信息
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JedisClusterRegistration.class, name = "jedis-cluster"),
        @JsonSubTypes.Type(value = JDBCRegistration.class, name = "jdbc"),
        @JsonSubTypes.Type(value = JedisRegistration.class, name = "jedis"),
        @JsonSubTypes.Type(value = JedisShardedRegistration.class, name = "jedis-sharded")
})
public abstract class Registration {

    /**
     * 应用名
     */
    private String application;

    /**
     * 维护者
     */
    private List<Owner> owners;

    /**
     * 元数据
     */
    private Map<String, Object> metadata = new HashMap<>();


    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Registration{");
        sb.append("application='").append(application).append('\'');
        sb.append(", owners=").append(owners);
        sb.append(", metadata=").append(metadata);
        sb.append(", domain='").append(domain).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
