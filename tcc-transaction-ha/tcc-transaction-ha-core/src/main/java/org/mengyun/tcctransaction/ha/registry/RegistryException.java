package org.mengyun.tcctransaction.ha.registry;

/**
 * Created by Lee on 2020/9/15 10:02.
 * aggregate-framework
 */
public class RegistryException extends RuntimeException {

    private static final long serialVersionUID = -3638625506885456245L;

    public RegistryException() {
        super();
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
