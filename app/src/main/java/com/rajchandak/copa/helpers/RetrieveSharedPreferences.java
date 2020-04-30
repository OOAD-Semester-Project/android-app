package com.rajchandak.copa.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.openid.appauth.AuthState;

import org.json.JSONException;

/**
 * Class that provides a method to restore the authState object stored in the local storage after user validation.
 */
public class RetrieveSharedPreferences {

    /**
     * Checks if SharedPreferences object exists, if it does, it gives back the AuthState object.
     * @param context
     * @return
     */
    @Nullable
    public AuthState restoreAuthState(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = preferences.getString("AUTH_STATE", "");
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;

    }
}
