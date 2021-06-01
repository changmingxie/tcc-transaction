package org.mengyun.tcctransaction.ha;

public interface SentinelController {

    default boolean degrade() {
        return false;
    }

}
