package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSCourse;

import java.lang.reflect.Type;

/**
 * Created by jlundang on 6/17/15.
 */
public class CourseDeserializer implements JsonDeserializer<CLMSCourse> {
    @Override
    public CLMSCourse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSCourse course = new CLMSCourse();
        course.setNid(json.getAsJsonObject().get("id").getAsInt());
        course.setName(json.getAsJsonObject().get("course-name").getAsString());
        if (!json.getAsJsonObject().get("course-icon").isJsonNull())
            course.setUrl(json.getAsJsonObject().get("course-icon").getAsString());

        if (!json.getAsJsonObject().get("product-name").isJsonNull())
            course.setProductName(json.getAsJsonObject().get("product-name").getAsString());

        if (!json.getAsJsonObject().get("product-icon").isJsonNull())
            course.setProductLogo(json.getAsJsonObject().get("product-icon").getAsString());


        return course;
    }
}
