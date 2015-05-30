package com.DigiOrg;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.DigiOrg.matt.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Theo on 4/6/2015.
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList<UserEvent> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<UserEvent> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.eventNameView = (TextView) convertView.findViewById(R.id.eventNameOutput);
            holder.dateView =(TextView) convertView.findViewById(R.id.dateOutput);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        //spacing distance, before the time
        String tempUpperStr = timeFormat.format(listData.get(position).getDate());
        String strDate = "         " + tempUpperStr.toUpperCase() +
                "\n  " + dateFormat.format(listData.get(position).getDate());

        if (listData.get(position).getDate().getDate() < 10)
            strDate = "         " + tempUpperStr.toUpperCase() +
                    "\n   " + dateFormat.format(listData.get(position).getDate());

            String tempName = timeFormat.format(listData.get(position).getDate());


        // find out how long the time is, because it varies in length. time and date formatted separately
        int timeIndex = -1;
        for (int i = 0; i < 22; i++) {
            if (strDate.charAt(i) == 'M')
                timeIndex = i;
            if (strDate.charAt(i) == 'T')
                timeIndex = i;
            if (strDate.charAt(i) == 'W')
                timeIndex = i;
            if (strDate.charAt(i) == 'F')
                timeIndex = i;
            if (strDate.charAt(i) == 'S')
                timeIndex = i;
        }

        //Toast.makeText(layoutInflater.getContext(), "timeIndex:" + timeIndex, Toast.LENGTH_LONG).show();
        SpannableString newStr = new SpannableString(strDate);
        newStr.setSpan(new RelativeSizeSpan(1.6f), 0, timeIndex, 0);
        newStr.setSpan(new ForegroundColorSpan(Color.BLUE), 0, timeIndex, 0);
        newStr.setSpan(new RelativeSizeSpan(1.15f), timeIndex, newStr.length(), 0);
        newStr.setSpan(new ForegroundColorSpan(Color.BLUE), timeIndex, newStr.length(), 0);

        String name = listData.get(position).getName();
        if (name.length() > 38) {
           name = name.substring(0, Math.min(name.length(), 35));
           name = name + "...";
        }


        holder.eventNameView.setText(name);
        holder.dateView.setGravity(Gravity.RIGHT);
        holder.dateView.setText(newStr);
        return convertView;
    }

    static class ViewHolder {
        TextView eventNameView;
        TextView dateView;
    }
}