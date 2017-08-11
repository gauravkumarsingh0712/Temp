package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;


/**
 * Created by gauravkumar.singh on 6/15/2017.
 */

public class SavedNotificationScreenAdapter extends RecyclerView.Adapter<SavedNotificationScreenAdapter.SavedNotificationViewHolder> {

    private Context mContext;

    public SavedNotificationScreenAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    @Override
    public SavedNotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_notification_message_for_large_screen_adapter, parent, false);
        SavedNotificationViewHolder viewHolder = new SavedNotificationViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SavedNotificationViewHolder holder, int position) {

        //   holder.videoNametextView.setText("Video Name");

    }



    public static class SavedNotificationViewHolder extends RecyclerView.ViewHolder {

        ImageView videoImageView;
        TextView videoNametextView;

        public SavedNotificationViewHolder(View view) {
            super(view);


        }
    }
}
