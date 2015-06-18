package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by jlundang on 6/18/15.
 */
public class CLMSClassList extends RealmObject {
    @Expose
    private String kind;
    @Expose
    @SerializedName("items")
    private RealmList<CLMSClass> classLists;

    public CLMSClassList() { }


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RealmList<CLMSClass> getClassLists() {
        return classLists;
    }

    public void setClassLists(RealmList<CLMSClass> classLists) {
        this.classLists = classLists;
    }
}
