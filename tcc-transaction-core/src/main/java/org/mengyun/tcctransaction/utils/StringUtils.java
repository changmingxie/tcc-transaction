package org.mengyun.tcctransaction.utils;

/**
 * Created by changmingxie on 11/11/15.
 */
public class StringUtils {

    public static boolean isNotEmpty(String value) {

        if (value == null) {
            return false;
        }

        if (value.equals("")) {
            return false;
        }

        return true;
    }

    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
