package org.mengyun.tcctransaction.spring.xid;

import com.xfvape.uid.UidGenerator;
import org.mengyun.tcctransaction.xid.UUIDGenerator;

public class DefaultUUIDGenerator implements UUIDGenerator {
    private UidGenerator uidGenerator;

    public DefaultUUIDGenerator(UidGenerator uidGenerator) {
        this.uidGenerator = uidGenerator;
    }

    @Override
    public String generate() {
        long value = this.uidGenerator.getUID();
        return Long.toHexString(value);
    }
}
