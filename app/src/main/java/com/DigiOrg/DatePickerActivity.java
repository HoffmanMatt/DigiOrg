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

import com.DigiOrg.matt.myapplication.R;


public class DatePickerActivity extends Activity {

    DatePicker datePicker;
    int day;
    int month;
    int year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        setTitle("Select date");
        //directs to next step of creation after saving the input
        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();
                Intent intent = new Intent(DatePickerActivity.this, CreateEvent.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                startActivity(intent);
            }
        });

        //directs back to main
        Button backButton = (Button) findViewById(R.id.cancel_button);
        backButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                Intent i = new Intent(DatePickerActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}