package org.mengyun.tcctransaction.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/25/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Compensable {

    public Propagation propagation() default Propagation.REQUIRED;

    public String confirmMethod() default "";

    public String cancelMethod() default "";

    public Class<? extends TransactionContextEditor> transactionContextEditor() default NullableTransactionContextEditor.class;

    class NullableTransactionContextEditor implements TransactionContextEditor {

        @Override
        public TransactionContext get() {
            return null;
        }

        @Override
        public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

        }
    }
}