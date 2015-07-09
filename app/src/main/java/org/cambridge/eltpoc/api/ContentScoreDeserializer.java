package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSContentScore;

import java.lang.reflect.Type;

/**
 * Created by etorres on 7/7/15.
 */
public class ContentScoreDeserializer implements JsonDeserializer<CLMSContentScore> {
    @Override
    public CLMSContentScore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSContentScore contentScore = new CLMSContentScore();
        contentScore.setCalcProgress(json.getAsJsonObject().get("calc-progress").getAsInt());
        contentScore.setId(json.getAsJsonObject().get("id").getAsInt());
        contentScore.setKind(json.getAsJsonObject().get("kind").getAsString());
        contentScore.setContentName(json.getAsJsonObject().get("content-name").getAsString());
        return contentScore;
    }
}
