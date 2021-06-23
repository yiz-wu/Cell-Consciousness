package com.example.ProgettoAMIF.model;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.model.notificationService.ToastAndStatusBarNotification;

import java.util.ArrayDeque;

public class IdleAlerter {

    private static final String TAG = "IdleAlerter";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private KeyguardManager keyguardManager;
    private INotificationService notificationService;

    private final int sensorSamplingPeriodInMillis = 3000;
    private final int checkMovementEveryNMilliSeconds = 15000;
    private double sumOfLastAccelerations = 0;
    private ArrayDeque<Double> lastAccelerations = null;

    private long lastMovement = 0;
    private final int alertInterval = 1000 * 60 * 10; // 10 minutes -> 600 seconds

    private boolean userWasUsingPhone = false;

    public IdleAlerter(Context context){
        this.context = context;
        if(Initiate())
            Activate();
    }

    private boolean Initiate() {
        notificationService = new ToastAndStatusBarNotification(context, "Idle Alert");
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        lastAccelerations = new ArrayDeque<>(checkMovementEveryNMilliSeconds / sensorSamplingPeriodInMillis);
        lastMovement = System.currentTimeMillis();

        // TYPE_LINEAR_ACCELERATION represent phone's acceleration excluding gravity.
        sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensor == null){
            Toast.makeText(context.getApplicationContext(), "Linear_Acceleration Sensor not valid.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Linear_Acceleration Sensor not valid.");
            return false;
        }
        return true;
    }

    private void Activate(){
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // if phone is locked, nothing to worry about, user is not using phone
                if(isPhoneLocked()){
                    userWasUsingPhone = false;
                    return;
                }

                // if it's unlocked and this measurement is the first one since user starts using, set lastMovement time
                if(!userWasUsingPhone){
                    userWasUsingPhone = true;
                    lastMovement = System.currentTimeMillis();
                }
                // if it's not the first measurement, just continue

                double module = Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1]);
                module = Math.sqrt(module*module + event.values[2]*event.values[2]);
                ElaborateAccelerationModule(module);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {      }
        };
        sensorManager.registerListener(sensorEventListener, sensor, sensorSamplingPeriodInMillis);
    }

    private void ElaborateAccelerationModule(double accelerationModule) {
        // if queue is full, pop the oldest one before adding the newest
        if(lastAccelerations.size() == checkMovementEveryNMilliSeconds / sensorSamplingPeriodInMillis){
            sumOfLastAccelerations -= lastAccelerations.remove();
        }
        sumOfLastAccelerations += accelerationModule;
        lastAccelerations.addLast(accelerationModule);

        // IF it's passed more than alertInterval time since lastMovement time, alert and reset lastMovement time.
        if(System.currentTimeMillis() > lastMovement + alertInterval){
            Alert("Alza la testa, Giu il telefono!");
            lastMovement = System.currentTimeMillis();
        }

        // IF there had been a movement, reset lastMovement time to current time  <-  if the medium of last 5 measurement > 0.2
        if(sumOfLastAccelerations / lastAccelerations.size() > 0.2)
            lastMovement = System.currentTimeMillis();
    }

    public void Deactive(){   sensorManager.unregisterListener(sensorEventListener);    }

    private void Alert(String Alertmsg){
            notificationService.sendNotificationToUser(Alertmsg);
    }

    private boolean isPhoneLocked() {
        return keyguardManager.isKeyguardLocked();
    }
}
