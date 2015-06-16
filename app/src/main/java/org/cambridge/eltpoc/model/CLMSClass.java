package org.cambridge.eltpoc.model;

import io.realm.RealmObject;

/**
 * Created by jlundang on 6/15/15.
 */
public class CLMSClass extends RealmObject {
    private String TYPE = "clms#class"; // This should be final
    private int id;
    private String className;
    private String classRole;
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
