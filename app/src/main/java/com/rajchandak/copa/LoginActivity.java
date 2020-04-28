package com.rajchandak.copa;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.helpers.APIError;
import com.rajchandak.copa.helpers.ErrorUtils;
import com.rajchandak.copa.helpers.RestEndpoints;
import com.rajchandak.copa.helpers.RetrieveSharedPreferences;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    // state
    AuthState mAuthState;

    public static final String LOG_TAG = "COPA_LOG";
    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    private static final String HANDLE_AUTHORIZATION_RESPONSE = "com.rajchandak.copa.HANDLE_AUTHORIZATION_RESPONSE";

    // KeyCloak configurations to be used by AppAuth
    private static final String AUTH_ENDPOINT = "https://copa-keycloak.herokuapp.com/auth/realms/copa/protocol/openid-connect/auth";
    private static final String TOKEN_ENDPOINT = "https://copa-keycloak.herokuapp.com/auth/realms/copa/protocol/openid-connect/token";
    private static final String REDIRECT_URI = "oauth://oauth2callback2";
    private static final String SCOPES = "profile";
    private static final String CLIENT_ID = "clipboard-server";

    // Node.js server to be used by retrofit
    //private static final String NODE_SERVER = "http://fierce-caverns-43797.herokuapp.com";
    private static final String NODE_SERVER = "http://clipboard-syncronization-app.appspot.com/";


    Button mAuthorizeBtn;

    private static Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Log.d(LOG_TAG, "oncreate");
        mAuthorizeBtn = (Button)findViewById(R.id.authorizeBtn);

        retrofit = new Retrofit.Builder()
                .baseUrl(NODE_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        // AppAuth
        enablePostAuthorizationFlows();

        mAuthorizeBtn.setOnClickListener(new AuthorizeListener(this));
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent");
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            Log.d(LOG_TAG, "checkIntent");

            String action = intent.getAction();
            switch (action) {
                case HANDLE_AUTHORIZATION_RESPONSE:
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        checkIntent(getIntent());
    }

    /**
     * Exchanges the code, for the {@link TokenResponse}.
     *
     * @param intent represents the {@link Intent} from the Custom Tabs or the System Browser.
     */
    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        Log.d(LOG_TAG, "handleAuthorizationResponse");
        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                            Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                        }
                    }
                }
            });
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); //[important] Clearing your editor before using it.
        editor.putString(AUTH_STATE,authState.toJsonString());
        editor.apply();

        enablePostAuthorizationFlows();
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }



    private void enablePostAuthorizationFlows() {
        RetrieveSharedPreferences retrieveSharedPreferences = new RetrieveSharedPreferences();
        mAuthState = retrieveSharedPreferences.restoreAuthState(getApplicationContext());
        if(mAuthState!=null)
        {
            Log.d(LOG_TAG,mAuthState.toJsonString());
        }

        if (mAuthState != null && mAuthState.isAuthorized()) {

            Log.d("Has authorized", mAuthState.toJsonString());

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

    /**
     * Kicks off the authorization flow.
     */
    public class AuthorizeListener implements Button.OnClickListener {

        private final LoginActivity mLoginActivity;

        public AuthorizeListener(@NonNull LoginActivity loginActivity) {
            mLoginActivity = loginActivity;
        }
        @Override
        public void onClick(View view) {

            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse(AUTH_ENDPOINT) /* auth endpoint */,
                    Uri.parse(TOKEN_ENDPOINT) /* token endpoint */
            );

            String clientId = CLIENT_ID;
            Uri redirectUri = Uri.parse(REDIRECT_URI);
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            );
            builder.setScopes(SCOPES);
            AuthorizationRequest request = builder.build();

            AuthorizationService authorizationService = new AuthorizationService(view.getContext());

            String action = HANDLE_AUTHORIZATION_RESPONSE;
            Intent postAuthorizationIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);
        }

    }

    public class SignOutListener implements Button.OnClickListener {

        private final LoginActivity mLoginActivity;

        public SignOutListener(@NonNull LoginActivity loginActivity) {
            mLoginActivity = loginActivity;
        }

        @Override
        public void onClick(View view) {
            mLoginActivity.mAuthState = null;
            mLoginActivity.clearAuthState();
            mLoginActivity.enablePostAuthorizationFlows();
        }
    }

}
