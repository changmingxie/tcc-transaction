package org.mengyun.tcctransaction.load;

import java.lang.annotation.*;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 19:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LoadInfo {
    String name();
}
