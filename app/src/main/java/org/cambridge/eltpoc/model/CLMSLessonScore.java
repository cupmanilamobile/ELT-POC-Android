package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSLessonScore extends RealmObject {
    @Expose
    @SerializedName("kind")
    private String kind;
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("lesson-name")
    private String lessonName;
    @Expose
    @SerializedName("calc-progress")
    private double calcProgress;

    @PrimaryKey
    private String uniqueId;
    private int classId;
    private int unitId;

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

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
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

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
