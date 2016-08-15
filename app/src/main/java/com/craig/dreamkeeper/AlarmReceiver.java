package com.craig.dreamkeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by craig on 7/21/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static Ringtone ringtone;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm Boradcast received");

        AlarmActivity inst = AlarmActivity.instance();
        if(!inst.isRunning) {
            Intent i = new Intent(context, AlarmActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        createAlarmDialog(inst);

    }

    public void createAlarmDialog(final Context context){
        Log.d("AlarmReceiver", "create the cancel dialog");

        new AlertDialog.Builder(context)
                .setTitle("Alarm")
                .setMessage("Ready to record you dream?")
                .setPositiveButton(R.string.wake_up, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ringtone.stop();
                        if(AlarmActivity.dreamIntent == null) {
                            AlarmActivity.dreamIntent = new Intent(context, DreamActivity.class);
                        }
                        context.startActivity(AlarmActivity.dreamIntent);
                    }
                })
                .setNegativeButton(R.string.snooze, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        ringtone.stop();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
