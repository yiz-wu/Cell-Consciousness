    package com.example.ProgettoAMIF.data;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ProgettoAMIF.interfaces.IFasciaOrariaExecutor;
import com.example.ProgettoAMIF.interfaces.IFasciaOrariaHandler;
import com.example.ProgettoAMIF.model.FasciaOrariaExecutor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

// need this dependency
//    implementation 'com.google.code.gson:gson:2.8.6'
public class FasciaOrariaHandler implements IFasciaOrariaHandler, Iterable<FasciaOraria>{

    private static final String TAG = "FasciaOrariaHandler";
    private static final String listKey = "listKey";

    private ArrayList<FasciaOraria> list = null;
    private IFasciaOrariaExecutor executor;
    private final Context context;

    public FasciaOrariaHandler(Context context) {
        this.context = context;
        initFasceOrarie();
    }

    @Override
    public void initFasceOrarie() {
        SharedPreferences sharedPreferences = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        String jsonObject = sharedPreferences.getString(listKey, "[]");

        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<FasciaOraria>>(){}.getType();
        list = gson.fromJson(jsonObject, collectionType);

        initFasciaOrariaIDpool();
    }

    private void initFasciaOrariaIDpool() {
        int max = 0;
        for ( FasciaOraria fo : list ) {
            if(fo.getID() > max)
                max = fo.getID();
        }
        FasciaOraria.IDpool = max + 1;
    }

    @Override
    public void saveFasceOrarie() {
        SharedPreferences sharedPreferences = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        editor.putString(listKey, gson.toJson(list));
        editor.apply();
    }

    @Override
    public int getSize() {
        if(list!=null)
            return list.size();
        return -1;
    }

    @Override
    public FasciaOraria getFasciaOraria(int index) {
        if(list!=null)
            return list.get(index);
        return null;
    }

    @Override
    public FasciaOraria getFasciaOrariaByID(int ID) {
        for ( FasciaOraria fo : list ) {
            if(fo.getID() == ID)
                return fo;
        }
        return null;
    }

    @Override
    public FasciaOraria getFasciaOrariaByName(String name) {
        for ( FasciaOraria fo : list ) {
            if(fo.getName().equals(name))
                return fo;
        }
        return null;
    }

    @Override
    public void addFasciaOraria(FasciaOraria fasciaOraria) {
        list.add(fasciaOraria);
    }

    @Override
    public void deleteFasciaOraria(int index) {
        list.remove(index);
    }

    @Override
    public void deleteFasciaOrariaByID(int ID) {
        for ( FasciaOraria fo : list ) {
            if(fo.getID() == ID)
                list.remove(fo);
        }
    }

    @Override
    public void deleteFasciaOrariaByName(String name) {
        for ( FasciaOraria fo : list ) {
            if(fo.getName().equals(name))
                list.remove(fo);
        }
    }

    @Override
    public void deleteAll() {
        list.clear();
        FasciaOraria.IDpool = 0;
        saveFasceOrarie();
    }

    @Override
    public void enableFasciaOraria(int ID) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        FasciaOraria target = getFasciaOrariaByID(ID);

        // set alarm for PendingIntent that start the Executor  identified by requestcode = ID
        Intent intent = new Intent(context, FasciaOrariaExecutor.class);
        intent.putExtra("Name", target.getName());
        intent.putExtra("EndHour", target.getEndHour());
        intent.putExtra("EndMinute", target.getEndMinute());
        intent.putExtra("MinutiPermessi", target.getMinutiPermessi());
        intent.putExtra("TipoNotifica", target.getNotificationType());
        PendingIntent startIntent = PendingIntent.getService(context, ID, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, target.getStartHour());
        calendar.set(Calendar.MINUTE, target.getStartMinute());
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startIntent);


        // set alarm for PendingIntent that tell the Executor to stop  identified by requestcode = -ID
        intent.putExtra("stop", "stop");
        PendingIntent stopIntent = PendingIntent.getService(context, -ID, intent, 0);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, target.getEndHour());
        calendar.set(Calendar.MINUTE, target.getEndMinute());
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, stopIntent);

        Log.i(TAG, "Alarm set.");
    }

    @Override
    public void disableFasciaOraria(int ID) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, FasciaOrariaExecutor.class);
        PendingIntent startIntent = PendingIntent.getService(context, ID, intent, 0);
        PendingIntent endIntent = PendingIntent.getService(context, -ID, intent, 0);

        alarmMgr.cancel(startIntent);
        alarmMgr.cancel(endIntent);
        Log.i(TAG, "Alarm canceled.");
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public void printList(String tag){
        Log.i(tag, toString());
    }

    @NonNull
    @Override
    public Iterator<FasciaOraria> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator<FasciaOraria>{

        private int index = 0;
        @Override
        public boolean hasNext() {
            return index < getSize();
        }

        @Override
        public FasciaOraria next() {
            return getFasciaOraria(index++);
        }
    }

}
