package org.cambridge.eltpoc.model;

/**
 * Created by etorres on 6/24/15.
 */
public class CLMSWebModel extends CLMSModel {
    private boolean hasInternetConnection = false;
    private boolean isCourseRetrieved = false;
    private boolean isSynced = false;

    public boolean isHasInternetConnection() {
        return hasInternetConnection;
    }

    public void setHasInternetConnection(boolean hasInternetConnection) {
        this.hasInternetConnection = hasInternetConnection;
    }

    public boolean isCourseRetrieved() {
        return isCourseRetrieved;
    }

    public void setIsCourseRetrieved(boolean isCourseRetrieved) {
        this.isCourseRetrieved = isCourseRetrieved;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setIsSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }
}
