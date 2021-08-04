package org.mengyun.tcctransaction.api;

public @interface EnableTcc {
    public Class<? extends TransactionContextEditor> transactionContextEditor() default NullableTransactionContextEditor.class;
}
