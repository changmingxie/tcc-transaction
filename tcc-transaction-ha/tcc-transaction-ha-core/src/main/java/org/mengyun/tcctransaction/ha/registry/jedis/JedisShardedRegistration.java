package org.mengyun.tcctransaction.ha.registry.jedis;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mengyun.tcctransaction.ha.registry.Registration;

import java.util.HashSet;
import java.util.Set;

/**
 * jedis 分片注册信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JedisShardedRegistration extends Registration {

    private Set<JedisNode> nodes = new HashSet<>();


}
