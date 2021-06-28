package com.example.ProgettoAMIF.Reminders;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ProgettoAMIF.UI.MainActivity;
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.notificationService.ToastNotification;
import com.example.eserciziobroadcastreceiver.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LightChecker extends Service{

    private static final String TAG = "LigntChecker";
    public static final String AlertMsg1 = "Your screen may be barking at you, turn down your phone's brightness!";
    public static final String AlertMsg2 = "The environment may be too bright, increase your phone's brightness!";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener lightEventListener;
    private INotificationService notificationService;

    private String isMIUIasString = "unKnown";
    private long lastAlertTimeStamp = 0;
    private final int alertIntervall = 6000; // 6 seconds


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "LightChecker onStartCommand.");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, getText(R.string.channelID).toString())
                        .setContentTitle("Light Checker")
                        .setContentText("Light Checker in work.")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setNotificationSilent()
                        .build();
        // Notification ID cannot be 0.
        // associate this service with a notification so it will become a Foreground Service
        startForeground(10, notification);

        if(Initialization())
            Activate();
        else
            stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Deactive();
    }

    private boolean Initialization() {
        Log.i(TAG, "LightChecker Initialization.");
        context = getApplicationContext();
        notificationService = new ToastNotification(context);

        // MIUI system has a different range for screen brightness than Android Standard
        // I used a string instead of a boolean, because maybe there will be other OS that use different range
        isMIUIasString = isMiUi()? "true" : "false";

        sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(sensor == null){
            Toast.makeText(context.getApplicationContext(), "Light Sensor not valid.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Lignt Sensor not valid.");
            return false;
        }
        return true;
    }

    private void Activate(){
        Log.i(TAG, "LightChecker Activate.");
        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // values[0]: Ambient light level in SI lux units
                CompareLightWithBrightness(event.values[0]);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {      }
        };
        sensorManager.registerListener(lightEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void Deactive(){
        Log.i(TAG, "LightChecker Deactive.");
        sensorManager.unregisterListener(lightEventListener);
    }


    private void CompareLightWithBrightness(float ambienteLumens) {
        // get current screen brightness
        int brightness;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Failed to get current Brightness");
            Deactive();
            return;
        }

        /*  We send alert in two extreme case
                1. screen brightness is low, but detected environment light is high
                2. screen brightness is high, but detected environment light is low
         */
        // MIUI 's scrren_brightness range is [0-4000]
        if(isMIUIasString.equals("true")){
            if(ambienteLumens <= 10 && brightness >= 100)
                Alert(AlertMsg1);
            if(ambienteLumens >= 300 && brightness <= 1000)
                Alert(AlertMsg2);
        }else{
        // others android device's screen brightness range is [0-255]
            if(ambienteLumens <= 10 && brightness >= 65)
                Alert(AlertMsg1);
            if(ambienteLumens >= 300 && brightness <= 190)
                Alert(AlertMsg2);
        }
    }

    private void Alert(String Alertmsg){
        // a time check in order to avoid spamming notification
        if(System.currentTimeMillis()-alertIntervall > lastAlertTimeStamp){
            lastAlertTimeStamp = System.currentTimeMillis();
            notificationService.sendNotificationToUser(Alertmsg);
        }
    }


    private boolean isMiUi() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }
    private String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
