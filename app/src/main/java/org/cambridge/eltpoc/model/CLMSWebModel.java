package org.cambridge.eltpoc.model;

/**
 * Created by etorres on 6/24/15.
 */
public class CLMSWebModel extends CLMSModel {
    private boolean hasInternetConnection = false;
    private boolean isSynced = false;
    private String syncMessage;

    public boolean isHasInternetConnection() {
        return hasInternetConnection;
    }

    public void setHasInternetConnection(boolean hasInternetConnection) {
        this.hasInternetConnection = hasInternetConnection;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setIsSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }

    public String getSyncMessage() {
        return syncMessage;
    }

    public void setSyncMessage(String syncMessage) {
        this.syncMessage = syncMessage;
    }
}
