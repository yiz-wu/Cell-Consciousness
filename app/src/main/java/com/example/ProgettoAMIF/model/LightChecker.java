package com.example.ProgettoAMIF.model;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.model.notificationService.ToastAndStatusBarNotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LightChecker {

    private static final String TAG = "LigntChecker";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener lightEventListener;
    private INotificationService notificationService;

    private String isMIUIasString = "unKnown";
    private long lastAlertTimeStamp = 0;
    private final int alertIntervall = 6000; // 6 seconds

    public LightChecker(Context context){
        this.context = context;
        Activate();
    }

    private void Activate(){
        notificationService = new ToastAndStatusBarNotification(context, "Light");

        // MIUI system has a different range for screen brightness
        // I used a string instead of a boolean, because maybe there is other OS that use a different range
        isMIUIasString = isMiUi()? "true" : "false";

        sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(sensor == null){
            Toast.makeText(context.getApplicationContext(), "Light Sensor not valid.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Lignt Sensor not valid.");
            return;
        }
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
        sensorManager.unregisterListener(lightEventListener);
    }

    private void CompareLightWithBrightness(float ambienteLumens) {
        // get current screen brightness
        int brightness = 0;
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
                Alert("Lo schermo ti sta abbaiando, abbassa la luminosita!");
            if(ambienteLumens >= 300 && brightness <= 1000)
                Alert("L'ambiente e' troppo luminoso, aumenta la luminosita!");
        }else{
        // others android device's screen brightness range is [0-255]
            if(ambienteLumens <= 10 && brightness >= 65)
                Alert("Lo schermo ti sta abbaiando, abbassa la luminosita!");
            if(ambienteLumens >= 300 && brightness <= 190)
                Alert("L'ambiente e' troppo luminoso, aumenta la luminosita!");
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

}
