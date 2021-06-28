    package com.example.ProgettoAMIF.fasciaoraria.data;

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
import com.example.ProgettoAMIF.fasciaoraria.model.ExecuteFasciaOrariaReceiver;
import com.example.ProgettoAMIF.fasciaoraria.model.FasciaOrariaExecutor;
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
    private static final String defaultFasceOrarie = "[{\"ID\":1,\"active\":false,\"endHour\":14,\"endMinute\":0,\"secondiPermessi\":5,\"name\":\"SCHOOL\",\"notificationType\":0,\"startHour\":8,\"startMinute\":0},{\"ID\":2,\"active\":false,\"endHour\":17,\"endMinute\":0,\"secondiPermessi\":10,\"name\":\"WORK\",\"notificationType\":1,\"startHour\":9,\"startMinute\":0},{\"ID\":3,\"active\":false,\"endHour\":23,\"endMinute\":0,\"secondiPermessi\":1,\"name\":\"READING\",\"notificationType\":2,\"startHour\":22,\"startMinute\":0}]";

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
        String jsonObject = sharedPreferences.getString(listKey, defaultFasceOrarie);

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

    public ArrayList<FasciaOraria> getList() { return list; }

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
        int startHour = target.getStartHour();
        int startMinute = target.getStartMinute();
        int endHour = target.getEndHour();
        int endMinute = target.getEndMinute();

        // set alarm for PendingIntent that start the Executor  identified by requestcode = ID
        Intent intent = new Intent(context, ExecuteFasciaOrariaReceiver.class);
        intent.putExtra("ID", target.getID());
        intent.putExtra("SecondiPermessi", target.getSecondiPermessi());
        intent.putExtra("TipoNotifica", target.getNotificationType());
//        intent.putExtra("StartHour", startHour);
//        intent.putExtra("StartMinute", startMinute);
//        intent.putExtra("EndHour", endHour);
//        intent.putExtra("EndMinute", endMinute);

        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        long startTimeMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        long endTimeMillis = calendar.getTimeInMillis();

        // se "ora" e' in mezzo nella fascia oraria
        //   1. lancio in esecuzione Executor
        //   2. settare alarme di stop per oggi
        //   3. settare alarme di start per domani -> incremento il giorno di 1
        if(startTimeMillis <= currentTimeMillis && currentTimeMillis <= endTimeMillis){
            //   1. lancio in esecuzione Executor
            Intent toExecutor = new Intent(context, FasciaOrariaExecutor.class);
            toExecutor.putExtra("SecondiPermessi", target.getSecondiPermessi());
            toExecutor.putExtra("TipoNotifica", target.getNotificationType());
            context.startService(toExecutor);

            //   2. settare alarme di stop per oggi
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            intent.putExtra("stop", "stop");
            intent.putExtra("RequestCode", -ID);
            PendingIntent stopExecuter = PendingIntent.getBroadcast(context, -ID, intent, 0);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), stopExecuter);

            //   3. settare alarme di start per domani -> incremento il giorno di 1
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            intent.removeExtra("stop");
            intent.putExtra("RequestCode", ID);
            PendingIntent startExecutor = PendingIntent.getBroadcast(context, ID, intent, 0);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), startExecutor);
            return;
        }

        // altrimenti do intent al Alarm Manager
        // se la fascia oraria e' prima di "ora", prenoto start e stop per domani -> incremento DAY_OF_YEAR di 1
        if(endTimeMillis <= currentTimeMillis){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        // altrimenti -> la fascia oraria e' dopo di "ora", prenoto start e stop per oggi -> non modifico nulla

        //   settare alarme di start
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        intent.putExtra("RequestCode", ID);
        PendingIntent startExecutor = PendingIntent.getBroadcast(context, ID, intent, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), startExecutor);

        //   settare alarme di stop
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        intent.putExtra("stop", "stop");
        intent.putExtra("RequestCode", -ID);
        PendingIntent stopExecuter = PendingIntent.getBroadcast(context, -ID, intent, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), stopExecuter);

        Log.i(TAG, "Alarm set.");
        return;
    }

    @Override
    public void disableFasciaOraria(int ID) {
        Log.i(TAG, "disabling FasciaOraria.");
        //   1. stoppare Executor
        //   2. cancellare alarme di start
        //   3. cancellare alarme di stop

        //   1. stoppare Executor
        Intent toExecutor = new Intent(context, FasciaOrariaExecutor.class);
        toExecutor.putExtra("stop", "stop");
        context.startService(toExecutor);


        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //   2. cancellare alarme di start
        Intent intent = new Intent(context, ExecuteFasciaOrariaReceiver.class);
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
