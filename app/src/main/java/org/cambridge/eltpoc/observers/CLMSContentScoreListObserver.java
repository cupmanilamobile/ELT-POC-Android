package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSContentScore;

import java.util.ArrayList;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSContentScoreListObserver extends CLMSModel {
    private ArrayList<CLMSContentScore> scoreList;

    public ArrayList<CLMSContentScore> getScoreList() {
        return scoreList;
    }

    public void setScoreList(ArrayList<CLMSContentScore> scoreList) {
        this.scoreList = scoreList;
    }
}
