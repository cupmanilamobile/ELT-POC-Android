package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jlundang on 6/22/15.
 */
public class CLMSUnitScore extends RealmObject {
    @Expose
    @SerializedName("kind")
    private String kind;
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("unit-name")
    private String unitName;
    @Expose
    @SerializedName("calc-progress")
    private double calcProgress;

    @PrimaryKey
    private String uniqueId;
    private int classId;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName =unitName;
    }

    public double getCalcProgress() {
        return calcProgress;
    }

    public void setCalcProgress(double calcProgress) {
        this.calcProgress = calcProgress;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
