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

import com.DigiOrg.matt.myapplication.R;


public class EditReminderDateTime extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder_date_time);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);
        setTitle("Select Starting Date");


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);

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

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerReminderDualEdit);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePickerReminderDualEdit);

        Button buttonBack = (Button) findViewById(R.id.button_reminderDualEdit_cancel);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditReminderDateTime.this, CreateReminderActivity.class);
                startActivity(intent);
            }
        });


        Button updateButton = (Button) findViewById(R.id.button_reminderDualEdit_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditReminderDateTime.this, CreateReminderActivity.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                int newHour = timePicker.getCurrentHour();
                int newMinute = timePicker.getCurrentMinute();
                int newYear = datePicker.getYear() - 1900;
                int newMonth = datePicker.getMonth();
                int newDay = datePicker.getDayOfMonth();
                editor.putInt("year", newYear);
                editor.putInt("month", newMonth);
                editor.putInt("day", newDay);
                editor.putInt("hour", newHour);
                editor.putInt("minute", newMinute);
                editor.putBoolean("isEdit", true);
                editor.apply();
                startActivity(intent);
            }
        });

    }


}
