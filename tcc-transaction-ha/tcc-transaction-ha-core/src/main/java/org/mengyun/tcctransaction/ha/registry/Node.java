package org.mengyun.tcctransaction.ha.registry;

import lombok.Data;

import java.io.Serializable;


@Data
public abstract class Node implements Serializable {

    private int port;
    private String host;
    private String password;


}
