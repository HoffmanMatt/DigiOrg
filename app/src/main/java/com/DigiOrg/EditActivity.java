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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class EditActivity extends Activity {

    UserEventHandler handler;
    Button editDateButton;
    int index;
    EditText eventNameEditText, commentEditText;
    TextView dateTextView, timeTextView;
    CheckBox priorityCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final MediaPlayer nextNoise = MediaPlayer.create(getApplicationContext(), R.raw.advance);
        final MediaPlayer alert = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        final MediaPlayer select = MediaPlayer.create(getApplicationContext(), R.raw.menuselect);
        final MediaPlayer tabSelect = MediaPlayer.create(getApplicationContext(), R.raw.button19);
        final MediaPlayer saveNoise = MediaPlayer.create(getApplicationContext(), R.raw.newsave);
        final MediaPlayer deniedNoise = MediaPlayer.create(getApplicationContext(), R.raw.denied);
        final MediaPlayer next2 = MediaPlayer.create(getApplicationContext(), R.raw.next);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean interfaceSounds = prefs.getBoolean("interfaceSounds", true);

        File directory = getFilesDir();
        final File file = new File(directory, "stored_data.txt");
        //first try to read the data file in
        try {
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objectStream = new ObjectInputStream(fileInput);
            handler = (UserEventHandler) objectStream.readObject();
            fileInput.close();
        } catch (Exception e) {
            //this should never happen, huge error
            e.printStackTrace();
        }


        // pull index from intent
        Bundle bundle = getIntent().getExtras();
        final int index = bundle.getInt("index");

        // retrieve all known information about the object using the index
        String comment = handler.eventListChrono.get(index).getComment();
        String eventName = handler.eventListChrono.get(index).getName();
        final Date date = handler.eventListChrono.get(index).getDate();
        boolean isPriority = handler.eventListChrono.get(index).isPriority();
        eventNameEditText = (EditText) findViewById(R.id.eventNameEditText);
        eventNameEditText.setText(eventName);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        commentEditText.setText(comment);
        if (commentEditText.getText().toString().trim().isEmpty())
            commentEditText.setHint("Additional Notes");
        priorityCheckBox = (CheckBox) findViewById(R.id.checkBoxEdit);
        priorityCheckBox.setChecked(isPriority);
        dateTextView = (TextView) findViewById(R.id.eventDateText);
        timeTextView = (TextView) findViewById(R.id.eventTimeText);
        setTitle("Editing " + eventName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE \nMMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        dateTextView.setText(dateFormat.format(date));
        timeTextView.setText(timeFormat.format(date));

        // direct to edit the date
        editDateButton = (Button) findViewById(R.id.editDateButton);
        editDateButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditActivity.this, EditDate.class);
                intent.putExtra("year", handler.eventListChrono.get(index).getDate().getYear());
                intent.putExtra("month", handler.eventListChrono.get(index).getDate().getMonth());
                intent.putExtra("day", date.getDate());
                intent.putExtra("hour", handler.eventListChrono.get(index).getDate().getHours());
                intent.putExtra("minute", handler.eventListChrono.get(index).getDate().getMinutes());
                intent.putExtra("isTime", false);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        //direct to edit the time
        Button editTimeButton = (Button) findViewById(R.id.editTimeButton);
        editTimeButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v) {
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditActivity.this, EditDate.class);
                intent.putExtra("year", handler.eventListChrono.get(index).getDate().getYear());
                intent.putExtra("month", handler.eventListChrono.get(index).getDate().getMonth());
                //Toast.makeText(getApplicationContext(), "day:" + intDate, Toast.LENGTH_LONG).show();
                intent.putExtra("day", date.getDate());
                intent.putExtra("hour", handler.eventListChrono.get(index).getDate().getHours());
                intent.putExtra("minute", handler.eventListChrono.get(index).getDate().getMinutes());
                intent.putExtra("time", handler.eventListChrono.get(index).getDate().getTime());
                intent.putExtra("isTime", true);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        priorityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (interfaceSounds)
                    select.start();
            }
        });

        //delete the event after confirming again
        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButtonEdit);
        deleteButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog alertDialog = new AlertDialog.Builder(EditActivity.this).create();
                alertDialog.setTitle("Confirm Removal");
                if (interfaceSounds)
                    alert.start();
                alertDialog.setMessage("Are you sure? It will be removed from the Priority list as well.");
                alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.removeEvent(index);
                        if (interfaceSounds)
                            select.start();
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
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        });


        //direct back to main screen
        ImageButton backButton = (ImageButton) findViewById(R.id.backButtonEdit);
        backButton.setOnClickListener (new View.OnClickListener(){
            public void onClick(View v){
                if (interfaceSounds)
                    next2.start();
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //updates the event
        ImageButton updateButton = (ImageButton) findViewById(R.id.updateEventButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //save changes first
                String strName = eventNameEditText.getText().toString();
                if (handler.diffNameMinusIndexEvent(strName, index)) {
                    if (interfaceSounds)
                        next2.start();
                    handler.updateComment(commentEditText.getText().toString(), index, strName);
                    handler.updatePriority(priorityCheckBox.isChecked(), index, handler.eventListChrono.get(index));
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
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    if (interfaceSounds)
                        deniedNoise.start();
                    Toast.makeText(getApplicationContext(), "Event name taken, choose another", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
