package org.mengyun.tcctransaction.repository.helper;

public interface CommandCallback<T> {
    T execute(RedisCommands commands);
}
