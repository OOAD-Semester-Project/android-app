package com.rajchandak.copa.viewpagerfragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nkzawa.emitter.Emitter;
import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.data.ItemObjects;

import org.json.JSONException;
import org.json.JSONObject;

public class MobileFragment extends BaseFragment {

    public MobileFragment () {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getData("mobile");

        mSocket.on("newData", onNewMobileData);
        mSocket.on("newDataArrived", onNewMobileDataDeleted);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Emitter.Listener onNewMobileData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("new_clip", args.toString());

                    JSONObject data = (JSONObject) args[0];
                    Log.d("new_clip_data",data.toString());
                    try {
                        // Get clip data
                        if(data.getString("fromType").equals("mobile"))
                        {
                            getData("mobile");
                        }


                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    public Emitter.Listener onNewMobileDataDeleted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("new_clip", args.toString());

                    JSONObject data = (JSONObject) args[0];
                    Log.d("new_clip_data",data.toString());
                    getData("mobile");

                }
            });
        }
    };
}