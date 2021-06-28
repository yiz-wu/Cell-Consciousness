package com.example.ProgettoAMIF.FasciaOraria.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class ExecuteFasciaOrariaReceiver extends BroadcastReceiver {
    private static final String TAG = "ExecuteFasciaOraria";

    public static final String TIPO_NOTIFICA = "TipoNotifica";
    public static final String SECONDI_PERMESSI = "SecondiPermessi";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive ");

        if(intent.hasExtra("stop")){
            Log.i(TAG, "stop Executor ");
        } else {
            Log.i(TAG, "start Executor, secondiPermessi : " +intent.getIntExtra(SECONDI_PERMESSI, 1));
        }
        // start/stop service
        Intent toExecutor = new Intent(context, FasciaOrariaExecutor.class);
        toExecutor.putExtra(SECONDI_PERMESSI, intent.getIntExtra(SECONDI_PERMESSI, 1));
        toExecutor.putExtra(TIPO_NOTIFICA, intent.getIntExtra(TIPO_NOTIFICA, 0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(toExecutor);
        } else {
            context.startService(toExecutor);
        }

        // prorogate alarm
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intent.getIntExtra("RequestCode", 0), intent, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }


}
