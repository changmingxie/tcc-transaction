package org.mengyun.tcctransaction.ha.registry.jdbc;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mengyun.tcctransaction.ha.registry.Registration;

/**
 * Created by Lee on 2020/9/12 10:49.
 * aggregate-framework
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JDBCRegistration extends Registration {

    private JDBCNode node;


}
