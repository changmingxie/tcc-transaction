package org.mengyun.tcctransaction.exception;

/**
 * @author Nervose.Wu
 * @date 2022/5/13 14:19
 */
public class RegistryException extends RuntimeException {

    public RegistryException() {
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }
}
