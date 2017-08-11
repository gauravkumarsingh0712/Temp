package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


import com.ncsavault.alabamavault.R;

import java.util.ArrayList;

public class TagGridviewAdapter extends BaseAdapter
{
    private ArrayList<Integer> listCountry;
    private Activity activity;
    private int mColumn;

    public TagGridviewAdapter(Activity activity, ArrayList<Integer> listCountry) {
        super();
        this.listCountry = listCountry;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listCountry.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {
        public Button tagButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.tag_view_layout, null);

            view.tagButton = (Button) convertView.findViewById(R.id.tag_button);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }
}