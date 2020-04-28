package com.rajchandak.copa.viewpagerfragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nkzawa.emitter.Emitter;
import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.view.ItemObjects;

import org.json.JSONException;
import org.json.JSONObject;

public class MobileFragment extends BaseFragment {

    public MobileFragment () {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSocket.on("newData", onNewMobileData);
        super.getData("mobile");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Emitter.Listener onNewMobileData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("new_clip", "run");

                    JSONObject data = (JSONObject) args[0];


                    try {
                        // Get clip data
                        if(data.getString("fromType").equals("mobile"))
                        {
                            Log.d("new_clip_mobile", data.toString());
                            ItemObjects item = new ItemObjects();
                            ClipDetails clip;
                            clip = new ClipDetails(data.getString("from"), data.getString("fromType"), data.getLong("timestamp"), data.getString("clipboardText"));
                            item.setName(clip.getClipboardText());
                            item.setDate(clip.getTimestamp());
                            list.add(item);
                            adapter.notifyDataSetChanged();
                        }


                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };
}