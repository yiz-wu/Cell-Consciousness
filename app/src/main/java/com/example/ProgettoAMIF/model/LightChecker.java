package com.example.ProgettoAMIF.model;

import android.app.Activity;
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
    private SensorEventListener lightEventListener;
    private INotificationService notificationService;
    private TextView tv;

    private float maximumRange;
    private String isMIUI = "unKnown";

    public LightChecker(Context context, TextView t){
        this.tv = t;
        this.context = context;
        Activate();
    }

    public void Activate(){
        notificationService = new ToastAndStatusBarNotification(context, "Light");

        sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor == null){
            Toast.makeText(context.getApplicationContext(), "Light Sensor not valid.", Toast.LENGTH_LONG);
            Log.e(TAG, "Lignt Sensor not valid.");
            return;
        }
        maximumRange = lightSensor.getMaximumRange();
        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // values[0]: Ambient light level in SI lux units
                CompareLightWithBrightness(event.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {      }

        };


        if(isMiUi()) {
            isMIUI = "true";
        }
        else {
            isMIUI = "false";
        }

        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    public boolean isMiUi() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public String getSystemProperty(String propName) {
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

    public void Deactive(){
        sensorManager.unregisterListener(lightEventListener);
    }

    private void CompareLightWithBrightness(float value) {
        int brightness = 0;
        try {
            // The screen backlight brightness between 0 and 255.
            brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to get current Brightness");
            Deactive();
            return;
        }

        tv.setText(isMIUI + "\nBrightness is : " + brightness + "\nlight sensor : " + value);


        // compare them
        // 1. brightness high  and  light low
        // 2. brightness low   and  light high
//        if(value > 500 && brightness < 25){
//            String Alertmsg = null;
//            Alert(Alertmsg);
//        }

        // ambiente buio -> brightness

    }

    private void Alert(String Alertmsg){
        notificationService.sendNotificationToUser(Alertmsg);
    }




}
