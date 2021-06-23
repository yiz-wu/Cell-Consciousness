package com.example.ProgettoAMIF.model;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.model.notificationService.ToastAndStatusBarNotification;

import java.util.ArrayDeque;

public class MovementChecker {

    private static final String TAG = "MovementChecker";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private KeyguardManager keyguardManager;
    private INotificationService notificationService;

    private long lastAlertTimeStamp = 0;
    private final int alertIntervall = 6000; // 6 seconds
    private double sumOf15Acceleration = 0;
    private ArrayDeque<Double> last15acceleration = null;
    // linear acceleration sensor responds +- 5 times per second, so I'am going to consider only last 3 seconds

    public MovementChecker(Context context){
        this.context = context;
        Activate();
    }

    private void Activate(){
        notificationService = new ToastAndStatusBarNotification(context, "Movement");
        last15acceleration = new ArrayDeque<>(15);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        // TYPE_LINEAR_ACCELERATION represent phone's acceleration excluding gravity.
        sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensor == null){
            Toast.makeText(context.getApplicationContext(), "Linear_Acceleration Sensor not valid.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Linear_Acceleration Sensor not valid.");
            return;
        }
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // if phone is locked, nothing to worry about, user is not using phone
                if(isPhoneLocked())
                    return;
                // if it's unlocked, user probably is using the phone -> try to detect movement

                // values[0]: Acceleration force along the x axis (excluding gravity).
                // values[1]: Acceleration force along the y axis (excluding gravity).
                // values[2]: Acceleration force along the z axis (excluding gravity).
                double module = Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1]);
                module = Math.sqrt(module*module + event.values[2]*event.values[2]);
                ElaborateTotalAcceleration(module);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {      }
        };
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void ElaborateTotalAcceleration(double accelerationModule) {
        // I'm considering only last 3 seconds's movement, that's +- 15 sensor measurement
        if(last15acceleration.size() == 15){
            sumOf15Acceleration -= last15acceleration.remove();
        }
        sumOf15Acceleration += accelerationModule;
        last15acceleration.addLast(accelerationModule);

        // with test, I found out that event when walking slowly, the medium is above 0.75
        if(sumOf15Acceleration / last15acceleration.size() > 0.75)
            Alert("Alza la testa, Giu il telefono!");
    }

    public void Deactive(){   sensorManager.unregisterListener(sensorEventListener);    }

    private void Alert(String Alertmsg){
        // a time check in order to avoid spamming notification
        if(System.currentTimeMillis()-alertIntervall > lastAlertTimeStamp){
            lastAlertTimeStamp = System.currentTimeMillis();
            notificationService.sendNotificationToUser(Alertmsg);
        }
    }

    private boolean isPhoneLocked() {
        return keyguardManager.isKeyguardLocked();
    }
}
