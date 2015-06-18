package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by jlundang on 6/17/15.
 */
public class CLMSCourseList extends RealmObject {
    @Expose
    private String kind;
    @Expose
    @SerializedName("items")
    private RealmList<CLMSCourse> courseLists;

    public CLMSCourseList() { }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RealmList<CLMSCourse> getCourseLists() {
        return courseLists;
    }

    public void setCourseLists(RealmList<CLMSCourse> courseLists) {
        this.courseLists = courseLists;
    }
}
