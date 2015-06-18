package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jlundang on 6/15/15.
 */
public class CLMSClass extends RealmObject {
    @Expose
    private String TYPE = "clms#class";
    @PrimaryKey
    @Expose
    private int id;
    @Expose
    @SerializedName("class-name")
    private String className;
    @Expose
    @SerializedName("class-role")
    private String classRole;
    @Expose
    @SerializedName("course-id")
    private int courseId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassRole() {
        return classRole;
    }

    public void setClassRole(String classRole) {
        this.classRole = classRole;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        // Do nothing
    }
}
