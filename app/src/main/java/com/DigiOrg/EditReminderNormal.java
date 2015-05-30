package com.DigiOrg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.Spinner;
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


public class EditReminderNormal extends Activity {


    UserEventHandler handler;
    Date globalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder_normal);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);

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
        final Button addButton = (Button) findViewById(R.id.button_EditReminderByDays_create);
        //final Button button_editTime = (Button) findViewById(R.id.button_EditReminderNormal_back);
        final Button button_showDateTime = (Button) findViewById(R.id.button_EditReminderNormal_dateOutput);
        final TextView freqStringOutput = (TextView) findViewById(R.id.textView_EditReminderNormal_frequencyOutput);
        final EditText eventName = (EditText) findViewById(R.id.eventText_EditReminderByDays_name);
        final TextView disabledOutput = (TextView) findViewById(R.id.textView_EditReminderByDays_deactiveOutput);
        final EditText comment = (EditText) findViewById(R.id.eventText_EditReminderByDays_comment);


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

        Bundle bundle = getIntent().getExtras();
        final int index = bundle.getInt("indexReminder");
        setTitle("Editing " + handler.eventListReminder.get(index).getName());


        final UserReminder editObj = handler.eventListReminder.get(index);
        if (editObj.getFrequencyType().equals("Minute") || editObj.getFrequencyType().equals("Minutes"))
            freqSpinner.setSelection(0);
        if (editObj.getFrequencyType().equals("Hour") || editObj.getFrequencyType().equals("Hours"))
            freqSpinner.setSelection(1);
        if (editObj.getFrequencyType().equals("Day") || editObj.getFrequencyType().equals("Days"))
            freqSpinner.setSelection(2);
        if (editObj.getFrequencyType().equals("Week") || editObj.getFrequencyType().equals("Weeks"))
            freqSpinner.setSelection(3);
        if (editObj.getFrequencyType().equals("Month") || editObj.getFrequencyType().equals("Months"))
            freqSpinner.setSelection(4);
        if (editObj.getFrequencyType().equals("Year") || editObj.getFrequencyType().equals("Years"))
            freqSpinner.setSelection(5);


        freqNumSpinner.setSelection(editObj.getFrequency()-1);
        eventName.setText(editObj.getName());
        if (!handler.eventListReminder.get(index).isActive()) {
            disabledOutput.setText("CURRENTLY DISABLED");
            disabledOutput.setTextColor(Color.RED);
            disabledOutput.setTextSize(20);
            disabledOutput.setGravity(Gravity.CENTER);
        }

        if (editObj.getComment().trim().isEmpty())
            comment.setHint("Additional Notes");
        else
            comment.setText(editObj.getComment());

        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        globalDate = editObj.getDate();
        String dateString = timeFormat.format(editObj.getDate()) + " On " + dateFormat.format(editObj.getDate());
        button_showDateTime.setText(dateString);
        button_showDateTime.setGravity(Gravity.CENTER);

        String freqNumStr = freqNumSpinner.getSelectedItem().toString();
        final String freqTypeStr = freqSpinner.getSelectedItem().toString();
        boolean is1 = false;
        if (freqNumStr.equals("1")){
            is1 = true;
        }

        String tab1String;
        if (is1)
            tab1String = "Repeat every " + freqNumStr + " " + freqTypeStr + " starting";
        else
            tab1String = "Repeat every " + freqNumStr + " " + freqTypeStr + "s starting:";

        freqStringOutput.setText(tab1String);
        freqStringOutput.setGravity(Gravity.CENTER);

        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setEnabled(!eventName.getText().toString().trim().isEmpty());
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
                freqStringOutput.setText(newTab1String);

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
                freqStringOutput.setText(newTab1String);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        button_showDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("Dindex", index);
                editor.putBoolean("isByDays", false);
                Intent intent = new Intent(EditReminderNormal.this, ReminderDualEdit.class);
                editor.apply();
                startActivity(intent);
            }
        });

        Button button_back = (Button) findViewById(R.id.button_EditReminderByDays_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("returnReminderTab", true);
                editor.apply();
                Intent intent = new Intent(EditReminderNormal.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                if (handler.diffNameMinusIndex(eventName.getText().toString(), index)) {
                    Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_SHORT).show();
                    handler.updateReminderComment(comment.getText().toString(), index);
                    handler.updateFrequency(1 + freqNumSpinner.getSelectedItemPosition(), index);
                    String freqStr = (String) freqSpinner.getSelectedItem();
                    handler.updateFrequencyType(freqStr, index);
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
                    Intent intent = new Intent(EditReminderNormal.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    if (interfaceSounds)
                        deniedNoise.start();
                    Toast.makeText(getApplicationContext(), "Name taken. Choose another.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



}
