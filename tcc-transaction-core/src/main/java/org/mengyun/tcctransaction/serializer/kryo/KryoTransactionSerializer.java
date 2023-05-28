package org.mengyun.tcctransaction.serializer.kryo;

import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.transaction.Transaction;

/**
 * Created by changming.xie on 9/18/17.
 */
public class KryoTransactionSerializer extends KryoPoolSerializer<Transaction> implements TransactionSerializer {
}
