package org.cambridge.eltpoc.model;

/**
 * Created by etorres on 6/24/15.
 */
public class CLMSWebModel extends CLMSModel {
    private boolean hasInternetConnection = false;

    public boolean isHasInternetConnection() {
        return hasInternetConnection;
    }

    public void setHasInternetConnection(boolean hasInternetConnection) {
        this.hasInternetConnection = hasInternetConnection;
    }
}
