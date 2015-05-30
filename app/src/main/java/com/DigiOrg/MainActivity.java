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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.DigiOrg.matt.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends Activity implements Serializable {

    UserEventHandler handler;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplication(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);
        final MediaPlayer nextSound = MediaPlayer.create(getApplicationContext(), R.raw.fnext);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);
        final boolean deleteOldEvents = prefs.getBoolean("deleteOldEvents", false);

        setTitle("Home screen");
        File directory = getFilesDir();
        final File file = new File(directory, "stored_data.txt");
        //file.delete();
        //first, try to read the data file in, or create a new one if failed.
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
        if (deleteOldEvents) {
            handler.deleteOldEvents();
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
        }
        //copy each vector in the handler to arraylist for xml processing
        //only does a shallow copy, but that is fine
        ArrayList<UserEvent> chronoArray = new ArrayList<UserEvent>(handler.eventListChrono);

        //ArrayAdapter<UserEventHandler> adapter = new ArrayAdapter<UserEventHandler>(getListView.getContext(), android.R.layout)
        final ListView lv1 = (ListView) findViewById(R.id.listViewChrono);
        lv1.setAdapter(new CustomListAdapter(this, chronoArray));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (interfaceSounds)
                    select.start();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        //sets up priority array
        ArrayList<UserEvent> priorityArray = new ArrayList<UserEvent>(handler.eventListPriority);
        final ListView lv2 = (ListView) findViewById(R.id.listViewPriority);
        lv2.setAdapter(new CustomListAdapterPriority(this, priorityArray));
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                if (interfaceSounds)
                    alert.start();
                CharSequence optionsMenu[] = new CharSequence[]{"Move up", "Move down", "Edit"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Selected: " + handler.eventListPriority.get(position).getName());
                builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (interfaceSounds)
                                select.start();
                            handler.movePriorityEventGreater(position);
                            Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_LONG).show();
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
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isPriority", true);
                            editor.apply();
                            startActivity(intent);
                        }
                        if (which == 1) {
                            if (interfaceSounds)
                                select.start();
                            handler.movePriorityEventLess(position);
                            Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_LONG).show();
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
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isPriority", true);
                            editor.apply();
                            startActivity(intent);
                        }
                        if (which == 2) {
                            if (interfaceSounds)
                                select.start();
                            int chronoPosition = handler.getChronoIndexFromPriorityIndex(position);
                            Intent intent = new Intent(MainActivity.this, EditActivity.class);
                            intent.putExtra("index", chronoPosition);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();

            }
        });
        ArrayList<UserReminder> reminderArray = new ArrayList<UserReminder>(handler.eventListReminder);
        final ListView lv3 = (ListView) findViewById(R.id.listViewReminder);
        lv3.setAdapter(new CustomListAdapterReminder(this, reminderArray));
        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                if (interfaceSounds)
                    alert.start();
                UserReminder userReminder = handler.eventListReminder.get(position);
                CharSequence optionsMenu[];
                if (userReminder.isActive())
                    optionsMenu = new CharSequence[]{"Deactivate", "Delete", "Edit"};
                else
                    optionsMenu = new CharSequence[]{"Activate", "Delete", "Edit"};

                if (userReminder.getFrequency() == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Selected: " + handler.eventListReminder.get(position).getName());
                    builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                if (interfaceSounds)
                                    select.start();
                                handler.eventListReminder.get(position).setActive(!handler.eventListReminder.get(position)._is_active);
                                Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_LONG).show();
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
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("returnReminderTab", true);
                                editor.apply();
                                startActivity(intent);
                            }
                            if (which == 1) {
                                if (interfaceSounds)
                                    alert.start();
                                AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog2.setTitle("Confirm Removal");
                                alertDialog2.setMessage("Are you sure? It will be permanently deleted.");
                                alertDialog2.setButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.removeReminder(position);
                                        Toast.makeText(getApplicationContext(), "Removing. . .", Toast.LENGTH_SHORT).show();
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
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("returnReminderTab", true);
                                        editor.apply();
                                        startActivity(intent);
                                    }
                                });
                                alertDialog2.show();
                            }
                            if (which == 2) {
                                if (interfaceSounds)
                                    select.start();
                                Intent intent = new Intent(MainActivity.this, EditReminderByDays.class);
                                intent.putExtra("indexReminder", position);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("returnReminderTab", true);
                                editor.apply();
                                startActivity(intent);
                            }
                        }
                    });
                    builder.show();

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Selected: " + handler.eventListReminder.get(position).getName());
                    builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                if (interfaceSounds)
                                    select.start();
                                handler.eventListReminder.get(position).setActive(!handler.eventListReminder.get(position)._is_active);
                                Toast.makeText(getApplicationContext(), "Updating. . .", Toast.LENGTH_LONG).show();
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
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("returnReminderTab", true);
                                editor.apply();
                                startActivity(intent);
                            }
                            if (which == 1) {
                                if (interfaceSounds)
                                    alert.start();
                                AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog2.setTitle("Confirm Removal");
                                alertDialog2.setMessage("Are you sure? It will be permanently deleted.");
                                alertDialog2.setButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.removeReminder(position);
                                        Toast.makeText(getApplicationContext(), "Removing. . .", Toast.LENGTH_SHORT).show();
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
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("returnReminderTab", true);
                                        editor.apply();
                                        startActivity(intent);
                                    }
                                });
                                alertDialog2.show();
                            }
                            if (which == 2) {
                                if (interfaceSounds)
                                    select.start();
                                Intent intent = new Intent(MainActivity.this, EditReminderNormal.class);
                                intent.putExtra("indexReminder", position);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("returnReminderTab", true);
                                editor.apply();
                                startActivity(intent);
                            }
                        }
                    });
                    builder.show();
                }

            }
        });

        //sets up the names of the tabs (tab1/chronological order) through programming instead of directly
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("test1");
        tabSpec1.setContent(R.id.Tab_chronological);
        tabSpec1.setIndicator("Events");
        tabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("test2");
        tabSpec2.setContent(R.id.Tab_calender);
        tabSpec2.setIndicator("Reminders");
        tabHost.addTab(tabSpec2);
        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("test3");
        tabSpec3.setContent(R.id.Tab_priority);
        tabSpec3.setIndicator("Priority");
        tabHost.addTab(tabSpec3);




        boolean is_priority = prefs.getBoolean("isPriority", false);
        if (is_priority)
            tabHost.setCurrentTab(2);
        boolean is_reminder = prefs.getBoolean("returnReminderTab", false);
        if (is_reminder)
            tabHost.setCurrentTab(1);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isPriority", false);
        editor.putBoolean("returnReminderTab", false);
        editor.apply();

        //directs to new activity screen when Create Event Button is pushed
        ImageButton addButton = (ImageButton) findViewById(R.id.AddEventButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (interfaceSounds)
                    alert.start();
                CharSequence optionsMenu[] = new CharSequence[]{"Create event", "Create continuous reminder"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select type");
                builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (interfaceSounds)
                                select.start();
                            Intent intent = new Intent(MainActivity.this, DatePickerActivity.class);
                            startActivity(intent);
                        }
                        if (which == 1) {
                            if (interfaceSounds)
                                select.start();
                            Intent intent = new Intent(MainActivity.this, CreateReminderActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                startActivity(new Intent(MainActivity.this, OptionsMenu.class));

            }
        });


    }
}
