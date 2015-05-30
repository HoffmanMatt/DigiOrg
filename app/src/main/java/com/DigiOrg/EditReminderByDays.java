package com.DigiOrg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.Date;
import java.util.Vector;


public class EditReminderByDays extends Activity {

    UserEventHandler handler;
    Date globalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder_by_days);

        Button addButton = (Button) findViewById(R.id.button_EditReminderByDays_create);
        Button backButton = (Button) findViewById(R.id.button_EditReminderByDays_back);
        Button editTimeButton = (Button) findViewById(R.id.button_reminderEditByDays_changeTimeByDays);
        TextView disabledOutput = (TextView) findViewById(R.id.textView_EditReminderByDays_deactiveOutput);
        final CheckBox mondayCheckbox = (CheckBox) findViewById(R.id.MondayCheckboxEdit);
        final CheckBox tuesdayCheckbox = (CheckBox) findViewById(R.id.TuesdayCheckboxEdit);
        final CheckBox wednesdayCheckbox = (CheckBox) findViewById(R.id.WednesdayCheckboxEdit);
        final CheckBox thursdayCheckbox = (CheckBox) findViewById(R.id.ThursdayCheckboxEdit);
        final CheckBox fridayCheckbox = (CheckBox) findViewById(R.id.FridayCheckboxEdit);
        final CheckBox saturdayCheckbox = (CheckBox) findViewById(R.id.SaturdayCheckboxEdit);
        final CheckBox sundayCheckbox = (CheckBox) findViewById(R.id.SundayCheckboxEdit);
        final EditText editTextName = (EditText) findViewById(R.id.eventText_EditReminderByDays_name);
        final EditText editTextComment = (EditText) findViewById(R.id.eventText_EditReminderByDays_comment);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplicationContext(), R.raw.denied);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);

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

        Bundle bundle = getIntent().getExtras();
        final int index = bundle.getInt("indexReminder");
        setTitle("Editing " + handler.eventListReminder.get(index).getName());

        final UserReminder userReminder = handler.eventListReminder.get(index);

        if (!userReminder.isActive()){
            disabledOutput.setText("   CURRENTLY DISABLED   ");
            disabledOutput.setTextColor(Color.RED);
            disabledOutput.setTextSize(22);
            disabledOutput.setGravity(Gravity.CENTER);
        }


        for (int i = 0; i < userReminder.getDays().size(); i++) {
            if (userReminder.getDays().get(i).equals("Monday"))
                mondayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Tuesday"))
                tuesdayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Wednesday"))
                wednesdayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Thursday"))
                thursdayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Friday"))
                fridayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Saturday"))
                saturdayCheckbox.setChecked(true);
            else if (userReminder.getDays().get(i).equals("Sunday"))
                sundayCheckbox.setChecked(true);
        }

        editTextName.setText(userReminder.getName());
        if (userReminder.getComment().trim().isEmpty())
            editTextComment.setHint("Additional Notes");
        else
            editTextComment.setText(userReminder.getComment());

        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        globalDate = userReminder.getDate();
        String dateString = timeFormat.format(userReminder.getDate()) + " On " + dateFormat.format(userReminder.getDate());
        editTimeButton.setText(dateString);
        editTimeButton.setGravity(Gravity.CENTER);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("returnReminderTab", true);
                editor.apply();
                Intent intent = new Intent(EditReminderByDays.this, MainActivity.class);
                startActivity(intent);
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler.diffNameMinusIndex(editTextName.getText().toString(), index)) {
                    if (interfaceSounds)
                        next2.start();
                    Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_SHORT).show();
                    handler.updateReminderComment(editTextComment.getText().toString(), index);
                    Vector<String> days = new Vector<String>();
                    if (mondayCheckbox.isChecked())
                        days.add("Monday");
                    if (tuesdayCheckbox.isChecked())
                        days.add("Tuesday");
                    if (wednesdayCheckbox.isChecked())
                        days.add("Wednesday");
                    if (thursdayCheckbox.isChecked())
                        days.add("Thursday");
                    if (fridayCheckbox.isChecked())
                        days.add("Friday");
                    if (saturdayCheckbox.isChecked())
                        days.add("Saturday");
                    if (sundayCheckbox.isChecked())
                        days.add("Sunday");
                    handler.eventListReminder.get(index).setDays(days);
                    handler.eventListReminder.get(index).setDate(globalDate);

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

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("returnReminderTab", true);
                    editor.apply();
                    Intent intent = new Intent(EditReminderByDays.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (interfaceSounds)
                        deniedNoise.start();
                    Toast.makeText(getApplicationContext(), "Name taken. Choose another.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        editTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("Dindex", index);
                editor.putBoolean("isByDays", true);
                Intent intent = new Intent(EditReminderByDays.this, ReminderDualEdit.class);
                editor.apply();
                startActivity(intent);
            }
        });

        mondayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        tuesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        wednesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        thursdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        fridayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        saturdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });
        sundayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });

    }


}
