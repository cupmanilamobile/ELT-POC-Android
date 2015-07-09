package org.cambridge.eltpoc.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by etorres on 7/8/15.
 */
public class CLMSContentURL extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    private int id = 0;
    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
