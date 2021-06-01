package org.mengyun.tcctransaction.ha.registry.jedis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.mengyun.tcctransaction.ha.registry.Node;

/**
 * Created by Lee on 2020/9/14 14:01.
 * aggregate-framework
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class JedisNode extends Node {

    private int database;

}
