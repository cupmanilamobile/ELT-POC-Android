package org.cambridge.eltpoc.observers;

/**
 * Created by etorres on 6/19/15.
 */
public interface Observer<T> {
    public void update(T model);
}
