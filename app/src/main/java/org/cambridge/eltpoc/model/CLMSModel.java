package org.cambridge.eltpoc.model;

import org.cambridge.eltpoc.observers.ModelObservable;
import org.cambridge.eltpoc.observers.Observer;

import java.util.ArrayList;

/**
 * Created by etorres on 6/19/15.
 */
public class CLMSModel implements ModelObservable {

    private ArrayList<Observer<CLMSModel>> observers = new ArrayList<Observer<CLMSModel>>();

    private boolean hasError = false;

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer ob : observers) {
            ob.update(this);
        }
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
