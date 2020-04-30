package com.rajchandak.copa.viewpagerfragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Using Inheritance.
 * This class extends the BaseFragment class to create the Desktop Fragment.
 */
public class MobileFragment extends BaseFragment {

    public MobileFragment () {
        super();
    }

    /**
     * Lifecycle-hook for this fragment for when the fragment is created and attached to its parent class.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getData("mobile");

        mSocket.on("newData", onNewMobileData);
        mSocket.on("newDataArrived", onNewMobileDataDeleted);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Callback method for the socket connection on "newData"
     */
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

    /**
     * Callback method for the socket connection on "newDataArrived"
     */
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