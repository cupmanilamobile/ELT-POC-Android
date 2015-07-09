package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by etorres on 7/7/15.
 */
public class CLMSLessonScoreList extends RealmObject {
    @Expose
    @SerializedName("kind")
    private String kind;
    @Expose
    @SerializedName("items")
    private RealmList<CLMSLessonScore> lessonScoreList;

    public CLMSLessonScoreList() { }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RealmList<CLMSLessonScore> getLessonScoreList() {
        return lessonScoreList;
    }

    public void setLessonScoreList(RealmList<CLMSLessonScore> lessonScoreList) {
        this.lessonScoreList = lessonScoreList;
    }
}
