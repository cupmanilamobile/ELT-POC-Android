package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSUnitScoreList extends RealmObject {
    @Expose
    @SerializedName("kind")
    private String kind;
    @Expose
    @SerializedName("items")
    private RealmList<CLMSUnitScore> unitScoreList;

    public CLMSUnitScoreList() { }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RealmList<CLMSUnitScore> getUnitScoreList() {
        return unitScoreList;
    }

    public void setUnitScoreList(RealmList<CLMSUnitScore> unitScoreList) {
        this.unitScoreList = unitScoreList;
    }
}
