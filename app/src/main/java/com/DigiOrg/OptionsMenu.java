package com.DigiOrg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.DigiOrg.matt.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class OptionsMenu extends Activity {

    UserEventHandler handler;
    boolean interfaceSounds;
    boolean hasBeenChanged;
    int popupMsg;
    int pushNotification;
    int alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);

        CheckBox interefaceSounds = (CheckBox) findViewById(R.id.interfaceSoundsCheckbox);
        CheckBox autoDelete = (CheckBox) findViewById(R.id.autoDeleteCheckbox);
        final Spinner spinnerAlertSounds = (Spinner) findViewById(R.id.spinner_options_alertSounds);
        final Spinner spinnerPopupMsg = (Spinner) findViewById(R.id.spinner_options_popupMsg);
        final Spinner spinnerPushNotification = (Spinner) findViewById(R.id.spinner_options_pushNotification);
        setTitle("Settings");


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        hasBeenChanged = prefs.getBoolean("hasBeenChanged", false);
        popupMsg = prefs.getInt("popupMsg", 0);
        pushNotification = prefs.getInt("pushNotification", 0);
        alerts = prefs.getInt("alerts", 0);
        Toast.makeText(getApplicationContext(), "alerts:" + alerts, Toast.LENGTH_LONG).show();
        spinnerAlertSounds.setSelection(alerts);
        spinnerPopupMsg.setSelection(popupMsg);
        spinnerPushNotification.setSelection(pushNotification);
        boolean autoDeleteBool = prefs.getBoolean("deleteOldEvents", false);
        interefaceSounds.setChecked(interfaceSounds);
        if (hasBeenChanged)
            autoDelete.setChecked(autoDeleteBool);
        else
            autoDelete.setChecked(false);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);


        interefaceSounds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("interfaceSounds", isChecked);
                editor.apply();
                interfaceSounds = isChecked;
                if (isChecked)
                    select.start();
            }
        });

        autoDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("deleteOldEvents", isChecked);
                editor.putBoolean("hasBeenChanged", true);
                hasBeenChanged = true;
                editor.apply();
            }
        });


        CharSequence alertSoundsArray[] = new CharSequence[] {"Use phone settings", "On", "On & vibrate", "Vibrate only", "Silent"};
        CharSequence popupMsgArray[] = new CharSequence[] {"On", "Off"};
        CharSequence pushNotificationArray[] = new CharSequence[] {"On", "Off"};
        ArrayAdapter<CharSequence> soundsAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, alertSoundsArray);
        ArrayAdapter<CharSequence> popupAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, popupMsgArray);
        ArrayAdapter<CharSequence> pushAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, pushNotificationArray);

        spinnerAlertSounds.setAdapter(soundsAdapter);
        spinnerPopupMsg.setAdapter(popupAdapter);
        spinnerPushNotification.setAdapter(pushAdapter);


        Button viewOldEvents = (Button) findViewById(R.id.button_optionsMenu_viewOld);
        viewOldEvents.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
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
                final int deleteVectorSize = handler.eventListDeleted.size();
                if (deleteVectorSize > 0) {
                    if (interfaceSounds)
                        alert.start();
                    CharSequence[] optionsMenu;// = new String[deleteVectorSize];
                    optionsMenu = new CharSequence[deleteVectorSize];
                    for (int i = 0; i < deleteVectorSize; i++) {
                        optionsMenu[i] = handler.eventListDeleted.get(i).getName();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(OptionsMenu.this);
                    builder.setTitle("Past events");

                    builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < deleteVectorSize; i ++) {
                                if (which == i) {
                                    if (interfaceSounds)
                                        select.start();
                                    Intent intent = new Intent(OptionsMenu.this, CreateEvent.class);
                                    int year = handler.eventListDeleted.get(i).getDate().getYear();
                                    int month = handler.eventListDeleted.get(i).getDate().getMonth();
                                    int day = handler.eventListDeleted.get(i).getDate().getDate();
                                    intent.putExtra("year", year);
                                    intent.putExtra("month", month);
                                    intent.putExtra("day", day);
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("needAddDelName", true);
                                    editor.putString("delName", handler.eventListDeleted.get(i).getName());
                                    editor.apply();
                                    startActivity(intent);
                                }
                            }

                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No deleted events", Toast.LENGTH_LONG).show();
                    if (interfaceSounds)
                        select.start();
                }

            }
        });




        ImageButton backButton = (ImageButton) findViewById(R.id.button_optionsMenu_back);
        backButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                popupMsg = spinnerPopupMsg.getSelectedItemPosition();
                pushNotification = spinnerPushNotification.getSelectedItemPosition();
                alerts = spinnerAlertSounds.getSelectedItemPosition();
                editor.putInt("popupMsg", popupMsg);
                editor.putInt("pushNotification", pushNotification);
                editor.putInt("alerts", alerts);
                editor.apply();
                Intent intent = new Intent(OptionsMenu.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
