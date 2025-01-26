package org.mengyun.tcctransaction.observe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nervose.Wu
 * @date 2024/2/23 15:51
 */
public abstract class AbstractObservable<T> implements Observable<T> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractObservable.class);
    protected final Set<Observer<T>> observers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected volatile boolean closed = false;

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void deleteObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public Set<Observer<T>> getObservers() {
        return observers;
    }

    @Override
    public void notifyObservers(T message) {

        if (closed) {
            return;
        }
        for (Observer<T> observer : observers) {
            try {
                observer.onMessage(message);
            } catch (Exception ignore) {
                //ignore
            }
        }
    }

    @Override
    public void close() {
        closed = true;
    }
}
