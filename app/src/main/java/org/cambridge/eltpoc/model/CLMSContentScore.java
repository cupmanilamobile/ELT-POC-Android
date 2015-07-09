package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jlundang on 6/22/15.
 */
public class CLMSContentScore extends RealmObject {
    @Expose
    @SerializedName("kind")
    private String kind = "";
    @Expose
    private int id;
    @Expose
    @SerializedName("content-name")
    private String contentName = "";
    @Expose
    @SerializedName("calc-progress")
    private double calcProgress;
    @Expose
    @SerializedName("time-accessed")
    private int timeAccessed;

    @PrimaryKey
    private String uniqueId;

    private int classId;
    private int unitId;
    private int lessonId;
    private String downloadedFile = "";

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

    public int getTimeAccessed() {
        return timeAccessed;
    }

    public void setTimeAccessed(int timeAccessed) {
        this.timeAccessed = timeAccessed;
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

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getDownloadedFile() {
        return downloadedFile;
    }

    public void setDownloadedFile(String downloadedFile) {
        this.downloadedFile = downloadedFile;
    }
}
