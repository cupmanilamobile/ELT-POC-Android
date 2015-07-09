package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSLessonScore;

import java.util.ArrayList;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSLessonScoreListObserver extends CLMSModel {
    private ArrayList<CLMSLessonScore> scoreList;

    public ArrayList<CLMSLessonScore> getScoreList() {
        return scoreList;
    }

    public void setScoreList(ArrayList<CLMSLessonScore> scoreList) {
        this.scoreList = scoreList;
    }
}
