package org.cambridge.eltpoc;

import android.app.Application;

import org.cambridge.eltpoc.model.CLMSLinkModel;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.CLMSClassListObserver;
import org.cambridge.eltpoc.observers.CLMSContentScoreListObserver;

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

    public void setWebModel(CLMSWebModel webModel) {
        this.webModel = webModel;
    }

    public CLMSUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CLMSUser currentUser) {
        this.currentUser = currentUser;
    }

    public CLMSClassListObserver getClassListObserver() {
        return classListObserver;
    }

    public void setClassListObserver(CLMSClassListObserver classListObserver) {
        this.classListObserver = classListObserver;
    }

    public CLMSLinkModel getLinkModel() {
        return linkModel;
    }

    public void setLinkModel(CLMSLinkModel linkModel) {
        this.linkModel = linkModel;
    }

    public CLMSContentScoreListObserver getContentScoreListObserver() {
        return contentScoreListObserver;
    }

    public void setContentScoreListObserver(CLMSContentScoreListObserver contentScoreListObserver) {
        this.contentScoreListObserver = contentScoreListObserver;
    }
}
