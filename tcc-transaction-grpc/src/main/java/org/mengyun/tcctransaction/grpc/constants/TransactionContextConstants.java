package org.mengyun.tcctransaction.grpc.constants;

import io.grpc.Metadata;

import static io.grpc.Metadata.BINARY_HEADER_SUFFIX;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 17:43
 */
public class TransactionContextConstants {
    private static final String TRANSACTION_CONTEXT = "TRANSACTION_CONTEXT";

    public static final Metadata.Key<byte[]> TRANSACTION_CONTEXT_HEADER_KEY = Metadata.Key.of((TRANSACTION_CONTEXT + BINARY_HEADER_SUFFIX).toLowerCase(), Metadata.BINARY_BYTE_MARSHALLER);

    private TransactionContextConstants() {
    }
}
