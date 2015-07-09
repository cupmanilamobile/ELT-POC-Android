package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSLessonScore;

import java.lang.reflect.Type;

/**
 * Created by etorres on 7/7/15.
 */
public class LessonScoreDeserializer implements JsonDeserializer<CLMSLessonScore> {
    @Override
    public CLMSLessonScore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSLessonScore lessonScore = new CLMSLessonScore();
        lessonScore.setCalcProgress(json.getAsJsonObject().get("calc-progress").getAsInt());
        lessonScore.setId(json.getAsJsonObject().get("id").getAsInt());
        lessonScore.setKind(json.getAsJsonObject().get("kind").getAsString());
        lessonScore.setLessonName(json.getAsJsonObject().get("lesson-name").getAsString());
        return lessonScore;
    }
}
