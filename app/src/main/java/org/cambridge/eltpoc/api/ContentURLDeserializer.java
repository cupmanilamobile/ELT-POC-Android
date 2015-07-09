package org.cambridge.eltpoc.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.cambridge.eltpoc.model.CLMSContentURL;

import java.lang.reflect.Type;

/**
 * Created by etorres on 7/8/15.
 */
public class ContentURLDeserializer implements JsonDeserializer<CLMSContentURL> {
    @Override
    public CLMSContentURL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CLMSContentURL contentURL = new CLMSContentURL();
        contentURL.setId(json.getAsJsonObject().get("id").getAsInt());
        contentURL.setUrl(json.getAsJsonObject().get("url").getAsString());
        return contentURL;
    }
}
