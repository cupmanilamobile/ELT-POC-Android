package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by jlundang on 6/22/15.
 */
public class CLMSUnitLessonScore extends RealmObject {
    @Expose
    private String kind;
    @PrimaryKey
    @Expose
    private int id;
    @Expose
    @SerializedName("content-name")
    private String contentName;
    @Expose
    @SerializedName("calc-progress")
    private double calcProgress;

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

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public double getCalcProgress() {
        return calcProgress;
    }

    public void setCalcProgress(double calcProgress) {
        this.calcProgress = calcProgress;
    }
}
