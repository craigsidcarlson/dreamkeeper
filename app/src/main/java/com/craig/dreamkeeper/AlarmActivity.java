package com.craig.dreamkeeper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.craig.dreamkeeper.model.Alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * Created by craig on 7/21/16.
 */
public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static TimePicker alarmTimePicker;
    private static ToggleButton alarmToggle;
    private static AlarmActivity inst;
    public static final String ALARM_FILE_NAME = "alarmSaveData.data";

    public static Alarm alarm;

    static boolean isRunning = false;

    public static Intent dreamIntent;

    public static AlarmActivity instance(){
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
        isRunning = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //Create the time picker and the switch to turn the alarm on and off
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);

        loadAlarm();

        alarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarm.turnOffAlarm();
                alarmToggle.setChecked(false);
            }
        });

        //Load old alarms if they exist otherwise set to current time and off
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dream_btn:
                //cancel the alarm
                alarmManager.cancel(pendingIntent);


                // User chose the "Settings" item, show the app settings UI...
                if(dreamIntent == null) {
                    dreamIntent = new Intent(this, DreamActivity.class);
                }
                startActivity(dreamIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void onToggleClicked(View view) {
        Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, 0);

        if(((ToggleButton) view).isChecked()){
            Log.d("Alarm Activity", "Alarm on");
            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calender.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            alarm.setHour(alarmTimePicker.getHour());
            Log.d("Alarm Activity", "Hour " + alarm.getHour());

            alarm.setMinute(alarmTimePicker.getMinute());
            alarm.turnOnAlarm();
            saveAlarm();
            alarmManager.set(AlarmManager.RTC,alarm.getAlarmTime(), pendingIntent);

        } else {
            alarmManager.cancel(pendingIntent);
            alarm.turnOffAlarm();
            saveAlarm();
            Log.d("AlarmActivity", "Alarm Off");
        }
    }

    private Alarm loadAlarm() {
        ObjectInput in;
        try {
            if(fileExistance()) {
                in = new ObjectInputStream(new FileInputStream(getFilesDir() + "/" +ALARM_FILE_NAME));
                alarm = (Alarm) in.readObject();
                in.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException c){
            c.printStackTrace();
        }

        if (alarm == null) {
            Calendar cal = Calendar.getInstance();
            alarm = new Alarm(cal);
        }
        alarmTimePicker.setMinute(alarm.getMinute());
        alarmTimePicker.setHour(alarm.getHour());
        alarmToggle.setChecked(alarm.isAlarmOn());
        return alarm;
    }

    private void saveAlarm(){
        ObjectOutput out;
        try{
            if(!fileExistance()){
                File file = new File(getFilesDir() + "/" + ALARM_FILE_NAME);
                file.createNewFile();
            }
            FileOutputStream fos = openFileOutput(ALARM_FILE_NAME, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(alarm);
            out.close();
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
//        Log.d("AlarmActivity", "Hour" + alarm.getHour());
//        Log.d("AlarmActivity", "Minute" + alarm.getMinute());

    }

    public boolean fileExistance(){
        File file = new File(getFilesDir() + "/" + ALARM_FILE_NAME);
        return file.exists();
    }

}
