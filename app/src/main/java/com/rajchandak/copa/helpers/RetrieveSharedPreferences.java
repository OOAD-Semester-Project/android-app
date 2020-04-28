package com.rajchandak.copa.helpers;
//
// Created by rajkc on 26-04-2020.
//

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.openid.appauth.AuthState;

import org.json.JSONException;

public class RetrieveSharedPreferences {

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
