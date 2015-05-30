package com.DigiOrg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.DigiOrg.matt.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class CreateReminderActivity extends Activity {

    UserEventHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        setTitle("Select Reminder Style");
        File directory = getFilesDir();
        final File file = new File(directory, "stored_data.txt");

        try {
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objectStream = new ObjectInputStream(fileInput);
            handler = (UserEventHandler) objectStream.readObject();
            //Toast.makeText(getApplicationContext(), "Object successfully read.", Toast.LENGTH_LONG).show();
            fileInput.close();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "No file found. New obj created.", Toast.LENGTH_LONG).show();
            handler = new UserEventHandler();
        }

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);


        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost2);
        tabHost.setup();
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("test1");
        tabSpec1.setContent(R.id.tab1);
        tabSpec1.setIndicator("Regular");
        tabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("test2");
        tabSpec2.setContent(R.id.tab2);
        tabSpec2.setIndicator("By Days");
        tabHost.addTab(tabSpec2);
        final Button addButton = (Button) findViewById(R.id.button_EditReminderByDays_create);
        final Button button_editTime = (Button) findViewById(R.id.button_reminderCreate_changeTimeByDays);
        final Button button_showDateTime = (Button) findViewById(R.id.button_EditReminderNormal_dateOutput);
        final CheckBox mon = (CheckBox)findViewById(R.id.MondayCheckboxEdit);
        final CheckBox tues = (CheckBox)findViewById(R.id.TuesdayCheckboxEdit);
        final CheckBox wed = (CheckBox)findViewById(R.id.WednesdayCheckboxEdit);
        final CheckBox thur = (CheckBox)findViewById(R.id.ThursdayCheckboxEdit);
        final CheckBox fri = (CheckBox)findViewById(R.id.FridayCheckboxEdit);
        final CheckBox sat = (CheckBox)findViewById(R.id.SaturdayCheckboxEdit);
        final CheckBox sun = (CheckBox)findViewById(R.id.SundayCheckboxEdit);

        final Spinner freqSpinner = (Spinner) findViewById(R.id.spinner_EditReminderNormal_frequencyType);
        String[] types = {"Minute", "Hour", "Day", "Week", "Month", "Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        freqSpinner.setAdapter(adapter);

        final Spinner freqNumSpinner = (Spinner) findViewById(R.id.spinner_EditReminderNormal_frequency);
        String[] theInts = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                            "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
                            "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
                            "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"};
        ArrayAdapter<String> intAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, theInts);
        freqNumSpinner.setAdapter(intAdapter);

        final TextView tab1Output = (TextView) findViewById(R.id.textView_EditReminderNormal_frequencyOutput);
        freqSpinner.setSelection(2);

        String freqNumStr = freqNumSpinner.getSelectedItem().toString();
        String freqTypeStr = freqSpinner.getSelectedItem().toString();
        boolean is1 = false;
        if (freqNumStr.equals("1")){
            is1 = true;
        }

        String tab1String;
        if (is1)
            tab1String = "Repeat every " + freqNumStr + " " + freqTypeStr + " starting";
        else
            tab1String = "Repeat every " + freqNumStr + " " + freqTypeStr + "s starting:";

        tab1Output.setText(tab1String);
        tab1Output.setGravity(Gravity.CENTER);

        ///////////////////////////////////
        //load and set pref if exists
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        boolean isEdit = prefs.getBoolean("isEdit", false);
        int year = 0, month = 0, day = 0, hour = 0, minute = 0;
        boolean is_edit = false;
        if (isEdit) {
            year = prefs.getInt("year", -1);
            month = prefs.getInt("month", -1);
            day = prefs.getInt("day", -1);
            hour = prefs.getInt("hour", -1);
            minute = prefs.getInt("minute", -1);
            is_edit = true;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isEdit", false);
            editor.apply();
        }
        final int inYear = year;
        final int inMonth = month;
        final int inDay = day;
        final int inHour = hour;
        final int inMinute = minute;

        final boolean isNewEdit = is_edit;
        Calendar cal = Calendar.getInstance();
        final int nowyear = cal.get(Calendar.YEAR);
        final int nowmonth = cal.get(Calendar.MONTH);
        final int nowday = cal.get(Calendar.DAY_OF_MONTH);
        final int nowhour = cal.get(Calendar.HOUR_OF_DAY);
        final int nowminute = cal.get(Calendar.MINUTE);
        final Date nowDate = new Date (nowyear-1900, nowmonth, nowday, nowhour, nowminute);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        if (is_edit) {
            Date date = new Date (year, month, day, hour, minute);
            button_editTime.setText("At:  " + timeFormat.format(date) + " ");
            String dateTimeOut = timeFormat.format(date) + " on " + dateFormat.format(date);
            button_showDateTime.setText(dateTimeOut);
        }
        else {
            button_editTime.setText("At:  " + timeFormat.format(nowDate) + " ");
            String dateTimeOut = timeFormat.format(nowDate) + " on " + dateFormat.format(nowDate);
            button_showDateTime.setText(dateTimeOut);
        }



        final TextView nameTextView = (TextView) findViewById(R.id.eventText_EditReminderByDays_name);
        final TextView commentTextView = (TextView) findViewById(R.id.eventText_EditReminderByDays_comment);
        addButton.setEnabled(false);

        //////////////////////


        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setEnabled(!nameTextView.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        freqNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //tab1Output.destroyDrawingCache();
                String newFreqNumStr = freqNumSpinner.getSelectedItem().toString();
                String newFreqTypeStr = freqSpinner.getSelectedItem().toString();
                String newTab1String;
                if (position == 0)
                    newTab1String = "Repeat every " + newFreqNumStr + " " + newFreqTypeStr + " starting";
                else
                    newTab1String = "Repeat every " + newFreqNumStr + " " + newFreqTypeStr + "s starting:";
                tab1Output.setText(newTab1String);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        freqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newFreqNumStr = freqNumSpinner.getSelectedItem().toString();
                String newFreqTypeStr = freqSpinner.getSelectedItem().toString();
                String newTab1String;
                if (newFreqNumStr.equals("1"))
                    newTab1String = "Repeat every " + newFreqNumStr + " " + newFreqTypeStr + " starting";
                else
                    newTab1String = "Repeat every " + newFreqNumStr + " " + newFreqTypeStr + "s starting:";
                tab1Output.setText(newTab1String);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTab = tabHost.getCurrentTab();
                if (currentTab == 1) {
                    //Toast.makeText(getApplicationContext(), "by days add", Toast.LENGTH_LONG).show();
                    Date dateNeeded;
                    if (isNewEdit)
                        dateNeeded = new Date (inYear, inMonth, inDay, inHour, inMinute);
                    else
                        dateNeeded = nowDate;


                    Vector<String> days = new Vector<String>();
                    if (mon.isChecked())
                        days.add("Monday");
                    if (tues.isChecked())
                        days.add("Tuesday");
                    if (wed.isChecked())
                        days.add("Wednesday");
                    if (thur.isChecked())
                        days.add("Thursday");
                    if (fri.isChecked())
                        days.add("Friday");
                    if (sat.isChecked())
                        days.add("Saturday");
                    if (sun.isChecked())
                        days.add("Sunday");

                    boolean badName = true;
                    if (handler.addReminderByDays(nameTextView.getText().toString(), dateNeeded, days, commentTextView.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Updating", Toast.LENGTH_LONG).show();
                        badName = false;

                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            ObjectOutputStream out = new ObjectOutputStream(outputStream);
                            out.writeObject(handler);
                            out.close();
                            outputStream.close();
                        } catch (IOException i) {
                            i.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Did not save file. Error.", Toast.LENGTH_LONG).show();
                        }

                        if (badName) {
                            if (interfaceSounds)
                                deniedNoise.start();
                            Toast.makeText(getApplicationContext(), "Reminder name taken, choose another", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent i = new Intent(CreateReminderActivity.this, MainActivity.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("returnReminderTab", true);
                            editor.apply();
                            if (interfaceSounds)
                                next2.start();
                            startActivity(i);
                        }
                    }
/*
                    else {
                        if (interfaceSounds)
                            deniedNoise.start();
                        Toast.makeText(getApplicationContext(), "Reminder name taken, choose another", Toast.LENGTH_LONG).show();

                    }*/
                }
                else {
                    Date dateNeeded;
                    if (isNewEdit)
                        dateNeeded = new Date (inYear, inMonth, inDay, inHour, inMinute);
                    else
                        dateNeeded = nowDate;
                    String freqType = (String) freqSpinner.getSelectedItem();
                    int freqNum = 1 + freqNumSpinner.getSelectedItemPosition();
                    boolean badName = true;
                    if (handler.addReminderNormal(nameTextView.getText().toString(), dateNeeded, freqNum, freqType, commentTextView.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Updating", Toast.LENGTH_LONG).show();
                        badName = false;
                    }
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        ObjectOutputStream out = new ObjectOutputStream(outputStream);
                        out.writeObject(handler);
                        out.close();
                        outputStream.close();
                    } catch (IOException i) {
                        i.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Did not save file. Error.", Toast.LENGTH_LONG).show();
                    }

                    if (badName) {
                        if (interfaceSounds)
                            deniedNoise.start();
                        Toast.makeText(getApplicationContext(), "Reminder name taken, choose another", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (interfaceSounds)
                            next2.start();
                        Intent i = new Intent(CreateReminderActivity.this, MainActivity.class);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("returnReminderTab", true);
                        editor.apply();
                        startActivity(i);
                    }
                }
            }
        });

        button_editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent i = new Intent(CreateReminderActivity.this, EditReminderDateTime.class);
                startActivity(i);
            }
        });

        final Button backButton = (Button) findViewById(R.id.button_EditReminderByDays_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent i = new Intent(CreateReminderActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        button_showDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent i = new Intent(CreateReminderActivity.this, EditReminderDateTime.class);
                startActivity(i);
            }
        });

        sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        tues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        thur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
    }

}


