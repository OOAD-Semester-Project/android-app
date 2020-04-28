package com.rajchandak.copa.viewpagerfragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.rajchandak.copa.R;
import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.data.DeleteResponse;
import com.rajchandak.copa.data.ItemObjects;
import com.rajchandak.copa.helpers.APIError;
import com.rajchandak.copa.helpers.ErrorUtils;
import com.rajchandak.copa.helpers.RestEndpoints;
import com.rajchandak.copa.helpers.RetrieveSharedPreferences;
import com.rajchandak.copa.view.Adapter;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rajchandak.copa.MainActivity.LOG_TAG;

public class BaseFragment extends Fragment {

    public ArrayList<ItemObjects> list;
    public Adapter adapter;
    public Paint p = new Paint();
    public RecyclerView recyclerView;

    public Retrofit retrofit;
    public RestEndpoints restEndpoints;

    public final String NODE_SERVER = "https://clipboard-syncronization-app.appspot.com";

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket(NODE_SERVER);
        } catch (URISyntaxException e) {
            Log.d("new_clip", e.toString());
        }
    }

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        list = new ArrayList<>();


        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        adapter = new Adapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        enableSwipe();

        // Connect to Socket endpoint
        mSocket.connect();

//        Connect.addMyBooleanListener2(new ConnectionBooleanChangedListener2() {
//            @Override
//            public void OnMyBooleanChanged2() {
//                // do something
//                list.clear();
//                getData();
//                Log.d("SOMETHING HAPPENED MOB", "OnMyBooleanChanged2");
//            }
//        });

        return recyclerView;
    }

    public void getData(final String type) {

        if(list!=null)
            list.clear();

        retrofit = new Retrofit.Builder()
                .baseUrl(NODE_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restEndpoints = retrofit.create(RestEndpoints.class);

        final String platformType = type;
        RetrieveSharedPreferences retrieveSharedPreferences = new RetrieveSharedPreferences();
        AuthState mAuthState = retrieveSharedPreferences.restoreAuthState(getActivity());
        AuthorizationService mAuthorizationService = new AuthorizationService(getActivity());

        mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
            @Override
            public void execute(
                    @Nullable String accessToken,
                    @Nullable String idToken,
                    @Nullable AuthorizationException ex) {
                if (ex != null){
                    // negotiation for fresh token failed
                    return;
                }

                Log.i(LOG_TAG, String.format("TODO: call get clips with [Access Token: %s, ID Token: %s]", accessToken, idToken));

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("token", accessToken);
                    mSocket.emit("join", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //TODO: Remove hardcoding of userID
                JWT jwt = new JWT(accessToken);
                String userID = jwt.getClaim("preferred_username").asString();
                Call<List<ClipDetails>> call = restEndpoints.getClips(String.format("Bearer %s", accessToken), userID);
                Log.d(LOG_TAG, "HTTP Request GetClips: "+call.request().toString());
                call.enqueue(new Callback<List<ClipDetails>>() {
                    @Override
                    public void onResponse(Call<List<ClipDetails>> call, retrofit2.Response<List<ClipDetails>> response) {
                        if (response.isSuccessful()) {
                            // clipDetailsList will have clipboard sent by the Node server
                            List<ClipDetails> clipDetailsList = response.body();
                            Log.d(LOG_TAG, "GET CLIPS SUCCEEDED: MOBILE");

                            for (int i = 0; i < clipDetailsList.size(); i++) {

                                ItemObjects item = new ItemObjects();
                                ClipDetails clip = clipDetailsList.get(i);
                                if(clip.getFromType().equals(platformType))
                                {
                                    item.setName(clip.getClipboardText());
                                    item.setDate(clip.getTimestamp());
                                    item.setID(clip.getID());
                                    item.setFromType(clip.getFromType());
                                    list.add(item);
                                }

                            }

                            adapter.notifyDataSetChanged();

                        }
                        else
                        {
                            APIError error = ErrorUtils.parseError(response);
                            StringBuffer errMsg = new StringBuffer();
                            errMsg.append("GET CLIPS FAILURE: ErrorCode: ")
                                    .append(response.code())
                                    .append(", ErrorMessage: ")
                                    .append(response.message())
                                    .append(", ")
                                    .append(error.getMessage());
                            Log.d(LOG_TAG+" ERROR ", errMsg.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ClipDetails>> call, Throwable t) {
                        Log.d(LOG_TAG+" ERROR ", t.getMessage());
                    }
                });
            }
        });


    }

    public void enableSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                final ItemObjects deletedModel = list.get(position);
                adapter.removeItem(position);

                // Tell server that the clip has been deleted

                RetrieveSharedPreferences retrieveSharedPreferences = new RetrieveSharedPreferences();
                AuthState mAuthState = retrieveSharedPreferences.restoreAuthState(getActivity());
                AuthorizationService mAuthorizationService = new AuthorizationService(getActivity());
                mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
                    @Override
                    public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {
                        if (ex != null){
                            Log.e(LOG_TAG, "Failed to get fresh tokens before delete request, "+ex.getMessage());
                            return;
                        }

                        Call<DeleteResponse> call = restEndpoints.deleteClip(String.format("Bearer %s",accessToken), deletedModel.getID(), deletedModel.getFromType());
                        call.enqueue(new Callback<DeleteResponse>() {
                            @Override
                            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                                if (response.isSuccessful()) {
                                    DeleteResponse deleteResponse = response.body();
                                    String status="";
                                    if (deleteResponse.getSuccess()){
                                        status = "SUCCESS";
                                    }
                                    else {
                                        status = "FAILED";
                                    }

                                    Log.d(LOG_TAG, "STATUS: "+status+", MESSAGE: "+deleteResponse.getMessage());
                                }
                                else
                                {
                                    APIError error = ErrorUtils.parseError(response);
                                    StringBuffer errMsg = new StringBuffer();
                                    errMsg.append("DELETE CLIP FAILURE: ErrorCode: ")
                                            .append(response.code())
                                            .append(", ErrorMessage: ")
                                            .append(response.message())
                                            .append(", ")
                                            .append(error.getMessage());
                                    Log.e(LOG_TAG, errMsg.toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                                Log.d(LOG_TAG, "DELETE CLIP FAILED: "+t.getMessage());
                            }
                        });
                    }
                });

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


}
