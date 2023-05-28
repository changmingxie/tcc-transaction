package org.mengyun.tcctransaction.springboot.starter;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nervose.Wu
 * @date 2022/7/6 14:04
 */
public class TccTransactionConfigurationFilter implements AutoConfigurationImportFilter {

    @Override
    public boolean[] match(String[] classes, AutoConfigurationMetadata autoConfigurationMetadata) {
        Set<String> skips = getSkips();
        boolean[] matches = new boolean[classes.length];
        for (int i = 0; i < classes.length; i++) {
            matches[i] = !skips.contains(classes[i]);
        }
        return matches;
    }

    private Set<String> getSkips() {
        Set<String> classes = new HashSet<>();
        classes.add("org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration");
        return Collections.unmodifiableSet(classes);
    }
}
