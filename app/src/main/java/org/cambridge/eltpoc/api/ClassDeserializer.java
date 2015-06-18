package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSClass;

import java.lang.reflect.Type;

/**
 * Created by jlundang on 6/18/15.
 */
public class ClassDeserializer implements JsonDeserializer<CLMSClass> {

    @Override
    public CLMSClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSClass c = new CLMSClass();
        c.setId(json.getAsJsonObject().get("id").getAsInt());
        c.setClassName(json.getAsJsonObject().get("class-name").getAsString());
        c.setClassRole(json.getAsJsonObject().get("class-role").getAsString());
        c.setCourseId(json.getAsJsonObject().get("course-id").getAsInt());
        return c;
    }
}
