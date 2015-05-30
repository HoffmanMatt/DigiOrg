package com.DigiOrg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import com.DigiOrg.matt.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;


public class EditDate extends Activity {


    UserEventHandler handler;
    DatePicker datePicker;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_date);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);

        Bundle bundle = getIntent().getExtras();
        final int index = bundle.getInt("index");
        int year = bundle.getInt("year");
        int month = bundle.getInt("month");
        int day = bundle.getInt("day");
        int hour = bundle.getInt("hour");
        int minute = bundle.getInt("minute");
        final boolean isTime = bundle.getBoolean("isTime");

        timePicker = (TimePicker) findViewById(R.id.timePickerReminderDualEdit);
        datePicker = (DatePicker) findViewById(R.id.datePickerReminderDualEdit);

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        datePicker.updateDate((year+1900), month, day);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHostReminderDualEdit);
        tabHost.setup();
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("test1");
        tabSpec1.setContent(R.id.Tab_reminderDualEdit_date);
        tabSpec1.setIndicator("Date");
        tabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("test2");
        tabSpec2.setContent(R.id.Tab_reminderDualEdit_time);
        tabSpec2.setIndicator("Time");
        tabHost.addTab(tabSpec2);
        if (isTime)
            tabHost.setCurrentTab(1);

        File directory = getFilesDir();
        final File file = new File(directory, "stored_data.txt");
        //try to read the data file in
        try {
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objectStream = new ObjectInputStream(fileInput);
            handler = (UserEventHandler) objectStream.readObject();
            fileInput.close();
        } catch (Exception e) {
            //this should never happen, huge error
            e.printStackTrace();
        }
        final String eventName = handler.eventListChrono.get(index).getName();
        setTitle("Editing " + eventName);

        //direct back to main screen
        Button backButton = (Button) findViewById(R.id.cancel_button_editDate);
        backButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditDate.this, EditActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        //Saves and exits
        Button updateButton = (Button) findViewById(R.id.button_reminderDualEdit_cancel);
        updateButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                int newHour = timePicker.getCurrentHour();
                int newMinute = timePicker.getCurrentMinute();
                int newYear = datePicker.getYear();
                int newMonth = datePicker.getMonth();
                int newDay = datePicker.getDayOfMonth();
                Date newDate = new Date ((newYear-1900), newMonth, newDay, newHour, newMinute);
                //pull date and time from pickers and store them at the index for update
                handler.updateDate(newDate, index, eventName);
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
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditDate.this, EditActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });


    }


}
