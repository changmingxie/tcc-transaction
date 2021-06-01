package org.mengyun.tcctransaction.ha.registry.jedis;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.mengyun.tcctransaction.ha.registry.Registration;

import java.util.HashSet;
import java.util.Set;

/**
 * jedis 集群 注册信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class JedisClusterRegistration extends Registration {

    private Set<JedisNode> nodes = new HashSet<>();


}
