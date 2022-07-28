package org.mengyun.tcctransaction.unittest.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.dashboard.dto.TransactionStoreDto;
import org.mengyun.tcctransaction.serializer.json.JacksonTransactionSerializer;
import org.mengyun.tcctransaction.transaction.Transaction;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.io.IOException;

/**
 * @Author huabao.fang
 * @Date 2022/7/21 23:10
 **/
public class JacksonTransactionSerializerTest {

    private JacksonTransactionSerializer serializer = new JacksonTransactionSerializer();

    @Test
    public void test() {
        Transaction transaction = new Transaction(new TransactionContext(
                "TCC:TEST",
                new TransactionXid("xxxxxxxxxxxxxx"),
                new TransactionXid("yyyyyyyyyyyyyy"),
                TransactionStatus.TRYING
        ));
        byte[] bytes = serializer.serialize(transaction);
        Transaction newTransaction = serializer.deserialize(bytes);

        assert transaction.getXid().equals(newTransaction.getXid());

    }


    /**
     * 问题：
     * com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `org.mengyun.tcctransaction.api.Xid` (no Creators, like default construct, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
     * at [Source: (byte[])"{"domain":null,"xid":{"xid":"xxxxxxxxxx"},"rootXid":null,"rootDomain":null,"content":null,"createTime":null,"lastUpdateTime":null,"version":0,"retriedCount":0,"statusId":0,"transactionTypeId":0,"xidString":null,"rootXidString":null}"; line: 1, column: 22] (through reference chain: org.mengyun.tcctransaction.dashboard.dto.TransactionStoreDto["xid"])
     * <p>
     * 解决：
     * 在TransactionStoreDto的xid上添加注解@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
     * {"domain":null,"xid":{"@class":"org.mengyun.tcctransaction.xid.TransactionXid","xid":"xxxxxxxxxx"},"rootXid":null,"rootDomain":null,"content":null,"createTime":null,"lastUpdateTime":null,"version":0,"retriedCount":0,"statusId":0,"transactionTypeId":0,"xidString":null,"rootXidString":null}
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TransactionStoreDto transactionStoreDto = new TransactionStoreDto();
        transactionStoreDto.setXid(new TransactionXid("xxxxxxxxxx"));
        byte[] bytes = objectMapper.writeValueAsBytes(transactionStoreDto);
//        System.out.println(new String(bytes));
        TransactionStoreDto newTransactionStoreDto = objectMapper.readValue(bytes, TransactionStoreDto.class);
        assert transactionStoreDto.getXid().equals(newTransactionStoreDto.getXid());
    }
}
