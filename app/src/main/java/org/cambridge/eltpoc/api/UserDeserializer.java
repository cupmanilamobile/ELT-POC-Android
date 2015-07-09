package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSUser;

import java.lang.reflect.Type;

/**
 * Created by etorres on 7/7/15.
 */
public class UserDeserializer implements JsonDeserializer<CLMSUser> {

    @Override
    public CLMSUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSUser user = new CLMSUser();
        user.setId(json.getAsJsonObject().get("id").getAsInt());
        user.setDisplayName(json.getAsJsonObject().get("display-name").getAsString());
        return user;
    }
}
