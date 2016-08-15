package com.craig.dreamkeeper.model;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by craig on 7/18/16.
 */

public class Alarm implements Serializable{
    private Calendar cal;
    private boolean alarmOn = false;

    public Alarm(Calendar cl) {
        cal = cl;
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public int getHour(){
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute(){
        return cal.get(Calendar.MINUTE);
    }

    public void setHour(int hourOfDay){
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    }

    public void setMinute(int minute){
        cal.set(Calendar.MINUTE, minute);
    }

    public Long getAlarmTime() {
        return cal.getTimeInMillis();
    }

    public void turnOnAlarm(){
        alarmOn = true;
    }

    public void turnOffAlarm(){
        alarmOn = false;
    }

    public boolean isAlarmOn(){
        return alarmOn;
    }


}
