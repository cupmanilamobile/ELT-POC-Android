package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSUnitScore;

import java.lang.reflect.Type;

/**
 * Created by etorres on 7/7/15.
 */
public class UnitScoreDeserializer implements JsonDeserializer<CLMSUnitScore> {
    @Override
    public CLMSUnitScore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSUnitScore unitScore = new CLMSUnitScore();
        unitScore.setCalcProgress(json.getAsJsonObject().get("calc-progress").getAsInt());
        unitScore.setId(json.getAsJsonObject().get("id").getAsInt());
        unitScore.setKind(json.getAsJsonObject().get("kind").getAsString());
        unitScore.setUnitName(json.getAsJsonObject().get("unit-name").getAsString());
        return unitScore;
    }
}
