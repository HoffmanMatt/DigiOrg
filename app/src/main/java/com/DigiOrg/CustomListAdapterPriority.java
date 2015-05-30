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
 * Created by machine-head on 4/14/15.
 */
public class CustomListAdapterPriority extends BaseAdapter {
    private ArrayList<UserEvent> listData;
    private LayoutInflater layoutInflater;

    UserEventHandler handler;
    Context context;

    public CustomListAdapterPriority(Context aContext, ArrayList<UserEvent> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        context = aContext;
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

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout_priority, null);
            holder = new ViewHolder();
            holder.eventNameView = (TextView) convertView.findViewById(R.id.eventNameOutputPriority);
            holder.dateView =(TextView) convertView.findViewById(R.id.dateOutputPriority);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = listData.get(position).getName();
        if (name.length() > 38) {
            name = name.substring(0, Math.min(name.length(), 35));
            name = name + "...";
        }
        holder.eventNameView.setText(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        //dateTextView.setText(dateFormat.format(date));
        String strDate = timeFormat.format(listData.get(position).getDate()) + "\n" +
                dateFormat.format(listData.get(position).getDate());
        holder.dateView.setGravity(Gravity.RIGHT);

        int max = 23;
        int timeIndex = -1;
        for (int i = 0; i < 22; i++) {
            if (strDate.charAt(i) == 'M') {
                timeIndex = i;
                i = max;
            }
            if (strDate.charAt(i) == 'T') {
                timeIndex = i;
                i = max;
            }
            if (strDate.charAt(i) == 'W') {
                timeIndex = i;
                i = max;
            }
            if (strDate.charAt(i) == 'F') {
                timeIndex = i;
                i = max;
            }
            if (strDate.charAt(i) == 'S') {
                timeIndex = i;
                i = max;
            }
        }

        timeIndex = timeIndex + 1;
        SpannableString newStr = new SpannableString(strDate);
        newStr.setSpan(new RelativeSizeSpan(1.6f), 0, timeIndex, 0);
        newStr.setSpan(new ForegroundColorSpan(Color.BLUE), 0, timeIndex, 0);
        newStr.setSpan(new RelativeSizeSpan(1.15f), timeIndex, newStr.length(), 0);
        newStr.setSpan(new ForegroundColorSpan(Color.BLUE), timeIndex, newStr.length(), 0);
        holder.dateView.setText(newStr);


        return convertView;
    }

    static class ViewHolder {
        TextView eventNameView;
        TextView dateView;
    }
}
