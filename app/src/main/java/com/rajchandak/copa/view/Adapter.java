package com.rajchandak.copa.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rajchandak.copa.R;
import com.rajchandak.copa.data.ItemObjects;

import java.util.ArrayList;

/**
 * Recycler View adapter to show the clipboard list to the user.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<ItemObjects> arrayList;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    public Adapter(Context ctx, ArrayList<ItemObjects> arrayList){

        inflater = LayoutInflater.from(ctx);
        this.arrayList = arrayList;
    }

    /**
     * Method to remove an item from the list.
     * @param position
     */
    public void removeItem(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

    /**
     * Uses polymorphism to handle the lifecycle-hook for when a view holder is created.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    /**
     * Lifecycle-hook for when the viewholder is bound to the fragment it resides in.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.content.setText(arrayList.get(position).getName());
        holder.from.setText("From Device: " + arrayList.get(position).getFrom());
        holder.date.setText(arrayList.get(position).getDate());
        holder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

                myClip = ClipData.newPlainText("label", holder.content.getText().toString());
                myClipboard.setPrimaryClip(myClip);
            }
        });

    }

    /**
     * Simple getter method to get the size of the list.
     * @return
     */
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    /**
     * Inner class that represents a single item in the list and binds the view with the corresponding XML elements.
     */
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        TextView from;
        TextView content;
        ImageButton copyButton;


        public MyViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            from = itemView.findViewById(R.id.from);
            content = itemView.findViewById(R.id.content);
            copyButton = itemView.findViewById(R.id.copyButton);

        }

    }
}
