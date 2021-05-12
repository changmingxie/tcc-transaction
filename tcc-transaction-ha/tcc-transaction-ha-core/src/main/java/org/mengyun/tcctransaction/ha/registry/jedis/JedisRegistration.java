package org.mengyun.tcctransaction.ha.registry.jedis;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mengyun.tcctransaction.ha.registry.Registration;

/**
 * Created by Lee on 2020/9/12 10:48.
 * aggregate-framework
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JedisRegistration extends Registration {

    private JedisNode node;


}
