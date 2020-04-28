package com.rajchandak.copa;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.rajchandak.copa.helpers.APIError;
import com.rajchandak.copa.helpers.ErrorUtils;
import com.rajchandak.copa.helpers.RestEndpoints;
import com.rajchandak.copa.helpers.RetrieveSharedPreferences;
import com.rajchandak.copa.services.ClipboardService;
import com.rajchandak.copa.viewpagerfragments.DesktopFragment;
import com.rajchandak.copa.viewpagerfragments.MobileFragment;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;

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
    private static final String NODE_SERVER = "https://clipboard-syncronization-app.appspot.com/";

    private static Retrofit retrofit;
    public RestEndpoints restEndpoints;

    private static boolean authorizationFlowInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(LOG_TAG, "oncreate");

        retrofit = new Retrofit.Builder()
                .baseUrl(NODE_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restEndpoints = retrofit.create(RestEndpoints.class);

        Log.d("authorizationFlow", authorizationFlowInitialized + "");
        if (!authorizationFlowInitialized)
        {
            authorizationFlowInitialized = true;
            authorizeListener();
        }
        // AppAuth
        //enablePostAuthorizationFlows();

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(AUTH_STATE);
        editor.apply();
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

            startService(new Intent(this, ClipboardService.class));
            Intent intent = new Intent("com.android.ServiceStopped");
            sendBroadcast(intent);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            viewPager = (ViewPager) findViewById(R.id.viewpagerMain);
            setupViewPager(viewPager);


            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            fragmentManager = getSupportFragmentManager();

        }
    }


    /**
     * Kicks off the authorization flow.
     */
    public void authorizeListener(){

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

        AuthorizationService authorizationService = new AuthorizationService(this);

        String action = HANDLE_AUTHORIZATION_RESPONSE;
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //String.format("Bearer %s",accessToken)
            Call<Void> call = restEndpoints.executeLogout();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(LOG_TAG, "Logout Successful");
                    }
                    else {
                        APIError error = ErrorUtils.parseError(response);
                        StringBuffer errMsg = new StringBuffer();
                        errMsg.append("LOGOUT FAILURE: ErrorCode: ")
                                .append(response.code())
                                .append(", ErrorMessage: ")
                                .append(response.message())
                                .append(", ")
                                .append(error.getMessage());
                        Log.e(LOG_TAG, errMsg.toString());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(LOG_TAG, t.getMessage());
                }
            });

            mAuthState = null;
            clearAuthState();
            finishAffinity();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DesktopFragment(), "Desktop");
        adapter.addFragment(new MobileFragment(), "Mobile");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {


        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
