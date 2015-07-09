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
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String USER_ID = "USER_ID";

    public static CLMSUser getLoggedInUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        CLMSUser user = new CLMSUser();
        user.setUsername(sharedPreferences.getString(USERNAME, ""));
        user.setPassword(sharedPreferences.getString(PASSWORD, ""));
        user.setId(sharedPreferences.getLong(USER_ID, 0));
        user.setDisplayName(sharedPreferences.getString(DISPLAY_NAME, ""));
        return user;
    }

    public static void updateLoggedInUser(Context context, String username, String password, String displayName,
                                          long userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putString(DISPLAY_NAME, displayName);
        editor.putLong(USER_ID, userId);
        editor.commit();
    }
}
