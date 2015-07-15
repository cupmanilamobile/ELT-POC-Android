package org.cambridge.eltpoc;

import android.app.Application;

import org.cambridge.eltpoc.model.CLMSLinkModel;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.CLMSClassListObserver;
import org.cambridge.eltpoc.observers.CLMSContentScoreListObserver;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;

/**
 * Created by etorres on 6/24/15.
 */
public class ELTApplication extends Application {
    private static ELTApplication instance;
    private CLMSWebModel webModel;
    private CLMSUser currentUser;
    private CLMSClassListObserver classListObserver;
    private CLMSContentScoreListObserver contentScoreListObserver;
    private CLMSLinkModel linkModel;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        webModel = new CLMSWebModel();
        classListObserver = new CLMSClassListObserver();
        linkModel = new CLMSLinkModel();
        contentScoreListObserver = new CLMSContentScoreListObserver();
    }

    public static ELTApplication getInstance() {
        return instance;
    }

    public CLMSWebModel getWebModel() {
        return webModel;
    }

    public CLMSUser getCurrentUser() {
        if(currentUser == null)
            return SharedPreferencesUtils.getLoggedInUser(getApplicationContext());
        return currentUser;
    }

    public void setCurrentUser(CLMSUser currentUser) {
        this.currentUser = currentUser;
    }

    public CLMSClassListObserver getClassListObserver() {
        return classListObserver;
    }

    public CLMSLinkModel getLinkModel() {
        return linkModel;
    }

    public CLMSContentScoreListObserver getContentScoreListObserver() {
        return contentScoreListObserver;
    }
}
