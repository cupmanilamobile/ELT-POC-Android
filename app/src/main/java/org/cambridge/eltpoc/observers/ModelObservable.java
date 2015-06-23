package org.cambridge.eltpoc.observers;

/**
 * Created by etorres on 6/19/15.
 */
public interface ModelObservable {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers();
}
