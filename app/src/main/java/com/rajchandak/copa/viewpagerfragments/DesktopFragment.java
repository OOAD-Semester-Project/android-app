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

public class DesktopFragment extends BaseFragment {

    public DesktopFragment () {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        super.getData("desktop");
        mSocket.on("newData", onNewDesktopData);
        mSocket.on("newDataArrived", onNewDesktopDataDeleted);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Emitter.Listener onNewDesktopData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("new_clip", "run");

                    JSONObject data = (JSONObject) args[0];

                    try {
                        // Get clip data
                        if(data.getString("fromType").equals("desktop"))
                        {
                            getData("desktop");
                        }


                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    public Emitter.Listener onNewDesktopDataDeleted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("new_clip", args.toString());

                    JSONObject data = (JSONObject) args[0];
                    Log.d("new_clip_data",data.toString());
                    getData("desktop");

                }
            });
        }
    };

}