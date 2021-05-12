package org.mengyun.tcctransaction.ha.registry.jdbc;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mengyun.tcctransaction.ha.registry.Node;

/**
 * Created by Lee on 2020/9/14 14:02.
 * aggregate-framework
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JDBCNode extends Node {

    private String username;
    private String url;


}
