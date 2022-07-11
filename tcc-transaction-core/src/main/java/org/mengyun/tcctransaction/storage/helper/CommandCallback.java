package org.mengyun.tcctransaction.storage.helper;

public interface CommandCallback<T> {
    T execute(RedisCommands commands);
}
