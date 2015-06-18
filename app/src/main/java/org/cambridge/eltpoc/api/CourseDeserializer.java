package org.cambridge.eltpoc.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.json.JSONArray;

import java.lang.reflect.Type;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit.RetrofitError;

/**
 * Created by jlundang on 6/17/15.
 */
public class CourseDeserializer implements JsonDeserializer<CLMSCourse> {
    @Override
    public CLMSCourse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSCourse course = new CLMSCourse();
        course.setNid(json.getAsJsonObject().get("nid").getAsInt());
        course.setName(json.getAsJsonObject().get("name").getAsString());
        if (!json.getAsJsonObject().get("url").isJsonNull())
            course.setUrl(json.getAsJsonObject().get("url").getAsString());

        if (!json.getAsJsonObject().get("product-name").isJsonNull())
            course.setProductName(json.getAsJsonObject().get("product-name").getAsString());

        if (!json.getAsJsonObject().get("product-logo").isJsonNull())
            course.setProductLogo(json.getAsJsonObject().get("product-logo").getAsString());


        return course;
    }
}
