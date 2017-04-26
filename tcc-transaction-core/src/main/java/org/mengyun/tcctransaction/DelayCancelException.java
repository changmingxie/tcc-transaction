package org.mengyun.tcctransaction;

/**
 * Created by changming.xie on 4/26/17.
 */
public class DelayCancelException extends RuntimeException {
    private static final long serialVersionUID = 8305637495103893260L;

    public DelayCancelException() {
        
    }

    public DelayCancelException(Throwable cause) {
        super(cause);
    }
}
