package org.mengyun.tcctransaction.unittest.utils;

import java.util.ArrayList;
import java.util.List;

public class TraceLog {

    private static List<String> messages = new ArrayList<>();

    public static void clear() {
        messages.clear();
    }

    public static void debug(String message) {
        messages.add(message);
        System.out.println(message);
    }

    public static List<String> getMessages() {
        return messages;
    }
}
