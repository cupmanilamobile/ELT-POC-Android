package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSModel;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSContentScoreListObserver extends CLMSModel {
    private boolean unitsRetrieved = false;
    private boolean lessonsRetrieved = false;
    private boolean contentsRetrieved = false;
    private int courseId = 0;
    private int classId = 0;

    public boolean isUnitsRetrieved() {
        return unitsRetrieved;
    }

    public void setUnitsRetrieved(boolean unitsRetrieved) {
        this.unitsRetrieved = unitsRetrieved;
    }

    public boolean isLessonsRetrieved() {
        return lessonsRetrieved;
    }

    public void setLessonsRetrieved(boolean lessonsRetrieved) {
        this.lessonsRetrieved = lessonsRetrieved;
    }

    public boolean isContentsRetrieved() {
        return contentsRetrieved;
    }

    public void setContentsRetrieved(boolean contentsRetrieved) {
        this.contentsRetrieved = contentsRetrieved;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public boolean allDetailsRetrieved() {
        return isUnitsRetrieved() && isLessonsRetrieved() && isUnitsRetrieved();
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void clearRetrieval() {
        setUnitsRetrieved(false);
        setContentsRetrieved(false);
        setLessonsRetrieved(false);
    }
}
