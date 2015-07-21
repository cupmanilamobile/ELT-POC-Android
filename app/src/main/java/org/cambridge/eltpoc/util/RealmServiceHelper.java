package org.cambridge.eltpoc.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import io.realm.RealmObject;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by etorres on 7/13/15.
 */
public class RealmServiceHelper {
    public static RestAdapter createAdapter(Type type, Object object, String url) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .setConverter(new GsonConverter(createGson(type, object)))
                .build();
        return restAdapter;
    }

    public static RestAdapter createAdapter(String url) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        return restAdapter;
    }

    public static Gson createGson(Type type, Object object) {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(type, object)
                .create();
    }

    public static String createBearerToken(String accessToken) {
        return "Bearer "+accessToken;
    }
}
