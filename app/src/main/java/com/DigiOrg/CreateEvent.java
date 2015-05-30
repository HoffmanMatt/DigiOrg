package com.DigiOrg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.DigiOrg.matt.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;


public class CreateEvent extends Activity implements Serializable {

    EditText nameText, commentText;
    CheckBox priorityBox;
    UserEventHandler handler;
    TimePicker timePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setTitle("Enter information");

        nameText = (EditText) findViewById(R.id.eventText_EditReminderByDays_name);
        final Button addButton = (Button) findViewById(R.id.add_event_button);
        final Button cancelButton = (Button) findViewById(R.id.back_button);
        addButton.setEnabled(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.fnext);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer saveNoise = MediaPlayer.create(getApplicationContext(), R.raw.save);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);
        /*
        if (prefs.getBoolean("needAddDelName", false)) {
            nameText.setText(prefs.getString("delName", "not found"));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("needAddDelName", false);
            editor.apply();
        }

        if (prefs.getBoolean("needSaveName", false)); {
            nameText.setText(prefs.getString("eventNameChange", "not found"));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("needSaveName", false);
            editor.apply();
        }*/

        commentText = (EditText) findViewById(R.id.eventText_EditReminderByDays_comment);
        priorityBox = (CheckBox) findViewById(R.id.priority_input);
        timePicker = (TimePicker) findViewById(R.id.timePicker);


        File directory = getFilesDir();
        final File handlerFile = new File(directory, "stored_data.txt");

        Bundle bundle = getIntent().getExtras();
        final int day = bundle.getInt("day");
        final int month = bundle.getInt("month");
        final int year = bundle.getInt("year");
        //try to read the data file in, or create a new one if failed.

        try {
            FileInputStream fileInput = new FileInputStream(handlerFile);
            ObjectInputStream objectStream = new ObjectInputStream(fileInput);
            handler = (UserEventHandler) objectStream.readObject();
            fileInput.close();
        } catch (Exception e) {
            handler = new UserEventHandler();
        }

        //Toast.makeText(getApplicationContext(), "." + nameText.getText().toString() + ".", Toast.LENGTH_LONG).show();


        // enables the add button only when an event name is entered
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setEnabled(!nameText.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        // add listener to addButton when clicked
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to set up date object properly for storage and display from datePicker and timePicker
                //int hour = timePicker.getCurrentHour();
                //int minute = timePicker.getCurrentMinute();
                //date = new SimpleDateFormat();
                //Date dateObj = new Date (year, month, day, hour, minute);
                Date dateObj = new Date ((year-1900), month, day, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                //date.set2DigitYearStart(dateObj);
                //Toast.makeText(getApplicationContext(), "About to add", Toast.LENGTH_LONG).show();

                //if add was successful, inform user, wait a second, save object, and return to main menu
                //Toast.makeText(getApplicationContext(), nameText.getText().toString() + ":" +
                        //handler.eventListChrono.get(0).getName(), Toast.LENGTH_LONG).show();
                boolean acceptable = true;
                if (nameText.getText().toString().trim().isEmpty()) {
                    acceptable = false;
                }
                if (handler.addEvent(nameText.getText().toString(), dateObj, commentText.getText().toString(), priorityBox.isChecked()) && acceptable) {
                    ///////////
                    Toast.makeText(getApplicationContext(), "Updating", Toast.LENGTH_LONG).show();

                    //need to save object to file and then return to main activity
                    try {
                        FileOutputStream outputStream = new FileOutputStream(handlerFile);
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
                    //return to main activity
                    Intent i = new Intent(CreateEvent.this, MainActivity.class);
                    startActivity(i);
                }
                // if add was not successful, need to tell user and try again
                else {
                    if (!acceptable) {
                        if (interfaceSounds)
                            deniedNoise.start();
                        Toast.makeText(getApplicationContext(), "Name can not be empty", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (interfaceSounds)
                            deniedNoise.start();
                        Toast.makeText(getApplicationContext(), "Event name taken, choose another", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        priorityBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });


        // add listener to cancelButton
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("needSaveName", true);
                editor.putString("eventNameChange", nameText.getText().toString());
                editor.apply();
                if (interfaceSounds)
                    next2.start();
                startActivity(new Intent(CreateEvent.this, DatePickerActivity.class));
            }
        });
    }
}
