package org.cambridge.eltpoc.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.cambridge.eltpoc.model.CLMSUser;

/**
 * Created by etorres on 6/18/15.
 */

public class SharedPreferencesUtils {
    private static final String USER_PREFERENCES = "USER_PREFERENCES";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static CLMSUser getLoggedInUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        CLMSUser user = new CLMSUser();
        user.setUsername(sharedPreferences.getString(USERNAME, ""));
        user.setPassword(sharedPreferences.getString(PASSWORD, ""));
        return user;
    }

    public static void updateLoggedInUser(Context context, String username, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.commit();
    }
}
