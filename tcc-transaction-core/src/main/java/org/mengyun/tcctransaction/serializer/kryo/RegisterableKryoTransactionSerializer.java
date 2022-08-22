package org.mengyun.tcctransaction.serializer.kryo;

import com.google.common.collect.Lists;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.transaction.InvocationContext;
import org.mengyun.tcctransaction.transaction.Participant;
import org.mengyun.tcctransaction.transaction.Transaction;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.util.List;

/**
 * Created by changming.xie on 9/18/17.
 */
public class RegisterableKryoTransactionSerializer extends RegisterableKryoSerializer<Transaction> implements TransactionSerializer {

    static List<Class> transactionClasses = Lists.newArrayList(
            Transaction.class,
            InvocationContext.class,
            TransactionXid.class,
            TransactionStatus.class,
            Participant.class,
            TransactionType.class);

    public RegisterableKryoTransactionSerializer() {
        this(transactionClasses);
    }

    public RegisterableKryoTransactionSerializer(int initPoolSize) {
        this(initPoolSize, transactionClasses);
    }

    public RegisterableKryoTransactionSerializer(List<Class> registerClasses) {
        super(CollectionUtils.merge(transactionClasses, registerClasses));
    }

    public RegisterableKryoTransactionSerializer(int initPoolSize, List<Class> registerClasses) {
        super(initPoolSize, CollectionUtils.merge(transactionClasses, registerClasses));
    }

    public RegisterableKryoTransactionSerializer(int initPoolSize, List<Class> registerClasses, boolean warnUnregisteredClasses) {
        super(initPoolSize, CollectionUtils.merge(transactionClasses, registerClasses), warnUnregisteredClasses);
    }
}
