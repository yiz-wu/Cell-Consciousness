package com.example.ProgettoAMIF.Reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class StopRemindersReceiver extends BroadcastReceiver {
    private static final String TAG = "StopRemindersReceiver";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String LIGHT_KEY = "light";
    public static final String MOVEMENT_KEY = "movement";
    public static final String IDLE_KEY = "idle";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receiver onReceive ");

        boolean stopReminders = intent.getBooleanExtra("stop", false);

        if(stopReminders){
            // stop reminders
            Intent stopLight = new Intent(context, LightChecker.class);
            Intent stopMovement = new Intent(context, MovementChecker.class);
            Intent stopIdle = new Intent(context, IdleChecker.class);
            context.stopService(stopLight);
            context.stopService(stopMovement);
            context.stopService(stopIdle);
            sharedPref = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putBoolean(LIGHT_KEY, false);
            editor.putBoolean(MOVEMENT_KEY, false);
            editor.putBoolean(IDLE_KEY, false);
            editor.apply();
            Log.i(TAG, "all reminders stopped");

            // set alarm for next day
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.SECOND, 0);

            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(context, 1, intent, 0));

        }

    }
}
