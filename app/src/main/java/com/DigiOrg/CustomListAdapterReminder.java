package com.DigiOrg;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.DigiOrg.matt.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by machine-head on 4/21/15.
 */
public class CustomListAdapterReminder extends BaseAdapter {
    private ArrayList<UserReminder> listData;
    private LayoutInflater layoutInflater;

    UserEventHandler handler;
    Context context;

    public CustomListAdapterReminder (Context aContext, ArrayList<UserReminder> listData) {
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
            convertView = layoutInflater.inflate(R.layout.list_row_layout_reminder, null);
            holder = new ViewHolder();
            holder.eventNameView = (TextView) convertView.findViewById(R.id.textView_layoutReminder_name);
            holder.frequencyView = (TextView) convertView.findViewById(R.id.textView_layoutReminder_frequencyOutput);
            holder.timeView = (TextView) convertView.findViewById(R.id.textView_layoutReminder_timeOutput);
            holder.dateView = (TextView) convertView.findViewById(R.id.textView_layoutReminder_dateOutput);
            holder.countdown = (TextView) convertView.findViewById(R.id.textView_layoutReminder_countdown);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = listData.get(position).getName();
        if (name.length() > 35) {
            name = name.substring(0, Math.min(name.length(), 32));
            name = name + "...";
        }
        holder.eventNameView.setText(name);

        holder.frequencyView.setTextColor(Color.BLUE);
        holder.timeView.setTextColor(Color.BLUE);
        holder.dateView.setTextColor(Color.BLUE);
        holder.countdown.setTextColor(Color.BLUE);
        holder.timeView.setGravity(Gravity.CENTER);
        holder.dateView.setGravity(Gravity.CENTER);


        //SETS UP FREQUENCY
        int freqNum = listData.get(position).getFrequency();
        if (freqNum != -1) {
            //  SET UP BY DAYS VIEW
            String freqType;
            if (freqNum > 1)
                freqType = listData.get(position).getFrequencyType() + "s";
            else
                freqType = listData.get(position).getFrequencyType();
            String freqString = "Every " + freqNum + " " + freqType;
            holder.frequencyView.setText(freqString);
            //////////////////
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR) - 1900;


            //////////////////////
            ;
            //SET UP COUNTDOWN OUTPUT
            Date reminderDateStart = listData.get(position).getDate();
            Date reminderDatePlusFreq = reminderDateStart;
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            final long year_in_millis = 365L * 24L * 60L * 60L * 1000;
            final long day_in_millis = 24L * 60L * 60L * 1000;
            final long hour_in_millis = 60L * 60L * 1000;
            final long minute_in_millis = 60L * 1000;
            final long second_in_millis = 1000;
            Date now = new Date(year, month, day, hour, minute);

            boolean isActive = listData.get(position).isActive();
            if (isActive) {
                String freqTypeString = listData.get(position).getFrequencyType();
                //Toast.makeText(context, "now:" + now.toString(), Toast.LENGTH_LONG).show();

                while (now.after(reminderDatePlusFreq)) {
                    if (freqTypeString.equals("Minute") || freqTypeString.equals("Minutes")) {
                        reminderDatePlusFreq = new Date(reminderDatePlusFreq.getTime() + freqNum * minute_in_millis);
                    }
                    else if (freqTypeString.equals("Hour") || freqTypeString.equals("Hours")) {
                        reminderDatePlusFreq = new Date(reminderDatePlusFreq.getTime() + freqNum * hour_in_millis);
                    }
                    else if (freqTypeString.equals("Day") || freqTypeString.equals("Days")) {
                        reminderDatePlusFreq = new Date(reminderDateStart.getTime() + freqNum * day_in_millis);
                    }
                    else if (freqTypeString.equals("Week") || freqTypeString.equals("Weeks")) {
                        Calendar tempCal = new GregorianCalendar();
                        tempCal.setTime(reminderDatePlusFreq);
                        tempCal.add(Calendar.WEEK_OF_YEAR, freqNum);
                        int zyear = tempCal.get(Calendar.YEAR) - 1900;
                        Date temppDate = new Date(zyear, tempCal.get(Calendar.MONTH),
                                tempCal.get(Calendar.DAY_OF_MONTH),
                                tempCal.get(Calendar.HOUR_OF_DAY),
                                tempCal.get(Calendar.MINUTE));
                        reminderDatePlusFreq = temppDate;

                    }
                    else if (freqTypeString.equals("Month") || freqTypeString.equals("Months")) {
                        Calendar tempCal = new GregorianCalendar();
                        tempCal.setTime(reminderDatePlusFreq);
                        tempCal.add(Calendar.MONTH, freqNum);
                        int zyear = tempCal.get(Calendar.YEAR) - 1900;
                        Date temppDate = new Date(zyear, tempCal.get(Calendar.MONTH),
                                tempCal.get(Calendar.DAY_OF_MONTH),
                                tempCal.get(Calendar.HOUR_OF_DAY),
                                tempCal.get(Calendar.MINUTE));
                        reminderDatePlusFreq = temppDate;

                    }
                    else if (freqTypeString.equals("Year") || freqTypeString.equals("Years")) {
                        Calendar tempCal = new GregorianCalendar();
                        tempCal.setTime(reminderDatePlusFreq);
                        tempCal.add(Calendar.YEAR, freqNum);
                        int zyear = tempCal.get(Calendar.YEAR) - 1900;
                        Date temppDate = new Date(zyear, tempCal.get(Calendar.MONTH),
                                tempCal.get(Calendar.DAY_OF_MONTH),
                                tempCal.get(Calendar.HOUR_OF_DAY),
                                tempCal.get(Calendar.MINUTE));
                        reminderDatePlusFreq = temppDate;
                    }
                }


                long diffInMillis = reminderDatePlusFreq.getTime() - now.getTime();
                long minutes = diffInMillis / minute_in_millis;
                long hours = diffInMillis / hour_in_millis;
                long days = diffInMillis / day_in_millis;
                long years = diffInMillis / year_in_millis;
                long seconds = diffInMillis / second_in_millis;
                if (minutes < 2) {
                    holder.countdown.setText("In: " + seconds + " seconds");
                }
                else if (minutes < 120) {
                    holder.countdown.setText("In: " + minutes + " minutes");
                }
                else if (hours < 49) {
                    holder.countdown.setText("In: " + hours + " hours");
                }
                else if (days < 1001)
                    holder.countdown.setText("In: " + days + " days");
                else
                    holder.countdown.setText("In: " + years + " to " + years+1 + " years");


                //SETS UP TIME OUTPUT
                //Date date = listData.get(position).getDate();
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                String timeString = "Next: " + timeFormat.format(reminderDatePlusFreq) + " ";
                holder.timeView.setText(timeString);
                //Toast.makeText(context, "mins:" + minutes, Toast.LENGTH_LONG).show();

                /////////////////

                //SETS UP DATE OUTPUT
                SimpleDateFormat dateFormat;
                boolean isDiffYear;
                if (reminderDatePlusFreq.getYear() != year) {
                    dateFormat = new SimpleDateFormat("MMM d, yyyy");
                    isDiffYear = true;
                }
                else {
                    dateFormat = new SimpleDateFormat("MMM d");
                    isDiffYear = false;
                }
                String dateString;
                if (isDiffYear)
                    dateString = dateFormat.format(reminderDatePlusFreq) + "  ";
                else
                    dateString = dateFormat.format(reminderDatePlusFreq) + "       ";
                holder.dateView.setText(dateString);

                //Toast.makeText(context, "mins:" + diffInMillis / minute_in_millis, Toast.LENGTH_LONG).show();

            }
            else
                reminderDatePlusFreq = new Date();

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("h:mm a MMM d, yyyy");
            //holder.countdown.setText(dateTimeFormat.format(reminderDatePlusFreq) + "\nnow:" + dateTimeFormat.format(now));
            /////////////
            if (!listData.get(position).isActive()) {
                holder.countdown.setText("CURRENTLY DISABLED");
                holder.countdown.setTextColor(Color.RED);
                //holder.countdown.setTextSize(14);
            }

            holder.frequencyView.setTextSize(16);
            holder.timeView.setTextSize(14);
            holder.dateView.setTextSize(14);


        }

        else {
            holder.dateView.setVisibility(View.GONE);
            holder.timeView.setVisibility(View.GONE);
            holder.dateView.invalidate();
            holder.timeView.invalidate();

            String daysString = "";
            if (listData.get(position).getDays().size() < 3) {
                holder.frequencyView.setTextSize(16);
                for (int i = 0; i < listData.get(position).getDays().size(); i++) {
                    daysString = daysString + listData.get(position).getDays().get(i) + "  ";
                    if (i != listData.get(position).getDays().size() -1)
                        daysString = "  " + daysString + "\n";
                }
            }
            else if (listData.get(position).getDays().size() == 7) {
                holder.frequencyView.setTextSize(19);
                for (int i = 0; i < listData.get(position).getDays().size(); i++) {
                    daysString = "\n" + "Daily           " + "\n";
                }
            }
            else if (listData.get(position).getDays().size() < 5) {
                holder.frequencyView.setTextSize(13);
                for (int i = 0; i < listData.get(position).getDays().size(); i++) {
                    daysString = daysString + listData.get(position).getDays().get(i) + "    ";
                    if (i != listData.get(position).getDays().size() -1)
                        daysString = "  " + daysString + "\n";
                }
            }
            else {
                holder.frequencyView.setTextSize(11);
                for (int i = 0; i < listData.get(position).getDays().size(); i++) {
                    daysString = daysString + listData.get(position).getDays().get(i) + "       ";
                    if (i != listData.get(position).getDays().size() - 1)
                        daysString = daysString + "\n";
                }
            }

            if (daysString.trim().equals(""))
                daysString = "\nNo days Selected!";

            holder.frequencyView.setText(daysString);
            holder.frequencyView.setGravity(Gravity.CENTER);

            ///////////////////
            if (listData.get(position).isActive()) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                String dateString = "At: " + timeFormat.format(listData.get(position).getDate());
                holder.countdown.setText(dateString);
            }
            else {
                holder.countdown.setText("CURRENTLY DISABLED");
                holder.countdown.setTextColor(Color.RED);
            }

        }

        return convertView;
    }

    static class ViewHolder {
        TextView eventNameView;
        TextView frequencyView;
        TextView timeView;
        TextView dateView;
        TextView countdown;
    }
}
