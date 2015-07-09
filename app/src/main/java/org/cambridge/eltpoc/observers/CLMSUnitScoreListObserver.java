package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUnitScore;

import java.util.ArrayList;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSUnitScoreListObserver extends CLMSModel {
    private ArrayList<CLMSUnitScore> scoreList;
    private int courseId;

    public ArrayList<CLMSUnitScore> getScoreList() {
        return scoreList;
    }

    public void setScoreList(ArrayList<CLMSUnitScore> scoreList) {
        this.scoreList = scoreList;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
