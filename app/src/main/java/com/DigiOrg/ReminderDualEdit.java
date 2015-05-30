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


public class ReminderDualEdit extends Activity {

    UserEventHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_dual_edit);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);


        Button backButton = (Button) findViewById(R.id.button_reminderDualEdit_cancel);
        Button saveButton = (Button) findViewById(R.id.button_reminderDualEdit_update);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePickerReminderDualEdit);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerReminderDualEdit);

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


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        final boolean isByDays = prefs.getBoolean("isByDays", false);
        final int index = prefs.getInt("Dindex", -1);
        setTitle("Editing " + handler.eventListReminder.get(index).getName());

        Date date = handler.eventListReminder.get(index).getDate();
        datePicker.updateDate(date.getYear()+1900, date.getMonth(), date.getDate());
        timePicker.setCurrentHour(date.getHours());
        timePicker.setCurrentMinute(date.getMinutes());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                Intent intent;
                if (isByDays)
                    intent = new Intent(ReminderDualEdit.this, EditReminderByDays.class);
                else
                    intent = new Intent(ReminderDualEdit.this, EditReminderNormal.class);
                intent.putExtra("indexReminder", index);
                editor.putBoolean("isByDays", false);
                editor.apply();
                startActivity(intent);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                int newHour = timePicker.getCurrentHour();
                int newMinute = timePicker.getCurrentMinute();
                int newYear = datePicker.getYear() - 1900;
                int newMonth = datePicker.getMonth();
                int newDay = datePicker.getDayOfMonth();
                Date newDate = new Date(newYear, newMonth, newDay, newHour, newMinute);
                handler.eventListReminder.get(index).setDate(newDate);

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
                Intent intent;
                if (isByDays)
                    intent = new Intent(ReminderDualEdit.this, EditReminderByDays.class);
                else
                    intent = new Intent(ReminderDualEdit.this, EditReminderNormal.class);
                intent.putExtra("indexReminder", index);
                editor.putBoolean("isByDays", false);
                editor.apply();
                startActivity(intent);
            }
        });

    }


}
