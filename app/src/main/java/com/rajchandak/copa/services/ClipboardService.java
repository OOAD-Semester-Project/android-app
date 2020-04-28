package com.rajchandak.copa.services;
//
// Created by rajkc on 22-02-2020.
//


import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.helpers.APIError;
import com.rajchandak.copa.helpers.DeviceInfo;
import com.rajchandak.copa.helpers.ErrorUtils;
import com.rajchandak.copa.helpers.RestEndpoints;
import com.rajchandak.copa.helpers.RetrieveSharedPreferences;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ClipboardService extends Service {
    private ClipboardManager mClipboardManager;
    private static Retrofit retrofit;
    private RestEndpoints restEndpoints;

    private static final String NODE_SERVER = "http://clipboard-syncronization-app.appspot.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("started", "onCreate");

        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipboardManager.addPrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }

        Log.d("COPA_LOG", "Service - onCreate");
        retrofit = new Retrofit.Builder()
                .baseUrl(NODE_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restEndpoints = retrofit.create(RestEndpoints.class);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        return START_STICKY;
    }


    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d("entered", "onPrimaryClipChanged");
                    final ClipData clip = mClipboardManager.getPrimaryClip();
                    if(mClipboardManager != null && clip != null && clip.getItemCount() > 0 && clip.getItemAt(0).getText()!=null)
                    {
                        if (mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
                                mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
                        {
                            Log.i("Copied Text", clip.getItemAt(0).getText().toString());
                            Toast.makeText(ClipboardService.this, "From COPA: Copied to clipboard.", Toast.LENGTH_SHORT).show();


                            // Post Request



                            RetrieveSharedPreferences retrieveSharedPreferences = new RetrieveSharedPreferences();
                            AuthState mAuthState = retrieveSharedPreferences.restoreAuthState(getApplicationContext());
                            AuthorizationService mAuthorizationService = new AuthorizationService(getApplicationContext());


                            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
                                @Override
                                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {
                                    if (ex != null) {
                                        Log.e("COPA_LOG", "Failed to get fresh tokens, "+ex.getMessage());
                                        return;
                                    }

                                    Long timestamp = System.currentTimeMillis();

                                    //NOTE: This must be mobile for mobile device
                                    String deviceType = "mobile";

                                    //NOTE: Recommend use of helper class to get device name
                                    String deviceName = DeviceInfo.getInstance().getDeviceName();

                                    String clipBoardText = clip.getItemAt(0).getText().toString();

                                    ClipDetails sendClipDetails = new ClipDetails(deviceType, deviceName, timestamp, clipBoardText);

                                    Call<Void> call = restEndpoints.sendClip(String.format("Bearer %s",accessToken),sendClipDetails);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Log.d("COPA_LOG", "ADD CLIP SUCCEEDED");
                                            }
                                            else
                                            {
                                                APIError error = ErrorUtils.parseError(response);
                                                StringBuffer errMsg = new StringBuffer();
                                                errMsg.append("ADD CLIP FAILURE: ErrorCode: ")
                                                        .append(response.code())
                                                        .append(", ErrorMessage: ")
                                                        .append(response.message())
                                                        .append(", ")
                                                        .append(error.getMessage());
                                                Log.e("COPA_LOG", errMsg.toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.d("COPA_LOG", "ADD CLIP FAILED: "+t.getMessage());
                                        }
                                    });
                                }
                            });







//                            OkHttpClient client = new OkHttpClient();
//                            Request request = new Request.Builder().url("ws://34.94.157.63:3000/mobileClient")
//                                    .addHeader("content-type", "application/json")
//                                    .build();
//                            EchoWebSocketListener listener = new EchoWebSocketListener();
//                            WebSocket ws = client.newWebSocket(request, listener);
//                            JSONObject message = new JSONObject();
//                            try {
//                                message.put("user", "adam");
//                                message.put("from", "mobile");
//                                message.put("message", clip.getItemAt(0).getText().toString());
//                                message.put("timestamp", System.currentTimeMillis());
//
//                            } catch (JSONException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            ws.send(message.toString());
//                            Log.d("MESSAGE: ", message.toString());
//                            Connect.setMyBoolean2(true);
//                            client.dispatcher().executorService().shutdown();
                        }
                    }


                }
            };


}