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
import android.util.Log;
import android.widget.Toast;

import com.rajchandak.copa.socket.Connect;
import com.rajchandak.copa.socket.EchoWebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class ClipboardService extends Service {
    private ClipboardManager mClipboardManager;

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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d("entered", "onPrimaryClipChanged");
                    ClipData clip = mClipboardManager.getPrimaryClip();
                    if(mClipboardManager != null && clip != null && clip.getItemCount() > 0 && clip.getItemAt(0).getText()!=null)
                    {
                        if (mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
                                mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
                        {
                            Log.i("Copied Text", clip.getItemAt(0).getText().toString());
                            Toast.makeText(ClipboardService.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();

                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url("ws://34.94.157.63:3000/mobileClient")
                                    .addHeader("content-type", "application/json")
                                    .build();
                            EchoWebSocketListener listener = new EchoWebSocketListener();
                            WebSocket ws = client.newWebSocket(request, listener);
                            JSONObject message = new JSONObject();
                            try {
                                message.put("user", "adam");
                                message.put("from", "mobile");
                                message.put("message", clip.getItemAt(0).getText().toString());
                                message.put("timestamp", System.currentTimeMillis());

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            ws.send(message.toString());
                            Log.d("MESSAGE: ", message.toString());
                            Connect.setMyBoolean2(true);
                            client.dispatcher().executorService().shutdown();
                        }
                    }


                }
            };


}