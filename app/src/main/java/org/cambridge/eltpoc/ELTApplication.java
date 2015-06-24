package org.cambridge.eltpoc;

import android.app.Application;

import org.cambridge.eltpoc.model.CLMSWebModel;

/**
 * Created by etorres on 6/24/15.
 */
public class ELTApplication extends Application {
    private static ELTApplication instance;
    private CLMSWebModel webModel;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        webModel = new CLMSWebModel();
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
}
