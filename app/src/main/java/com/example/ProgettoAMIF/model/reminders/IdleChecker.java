package com.example.ProgettoAMIF.model.reminders;

import android.app.KeyguardManager;
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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ProgettoAMIF.UI.MainActivity;
import com.example.ProgettoAMIF.UI.MainActivity2;
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.model.notificationService.ToastAndStatusBarNotification;
import com.example.eserciziobroadcastreceiver.R;

import java.util.ArrayDeque;

public class IdleChecker extends Service{

    private static final String TAG = "IdleAlerter";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private KeyguardManager keyguardManager;
    private INotificationService notificationService;

    private final int sensorSamplingPeriodInMillis = 3000;  // measure every 3 seconds
    private final int milliSecondsInConsideration = 15000;  // considerate last 15 seconds -> 5 measurement
    private double sumOfLastAccelerations = 0;
    private ArrayDeque<Double> lastAccelerations = null;

    private long lastMovement = 0;
//    private final int alertInterval = 1000 * 60 * 10; // 10 minutes -> 600 seconds

    private final int alertInterval = 30000;  // fot test purpose

    private boolean userWasUsingPhone = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "IdleChecker onStartCommand.");
        context = this;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, getText(R.string.channelID).toString())
                        .setContentTitle("Idle Checker")
                        .setContentText("Idle Checker in work.")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setNotificationSilent()
                        .build();
        // Notification ID cannot be 0.
        // associate this service with a notification so it will become a Foreground Service
        startForeground(12, notification);

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
        Log.i(TAG, "IdleChecker Initialization.");
        notificationService = new ToastAndStatusBarNotification(context, "Idle Alert");
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        lastAccelerations = new ArrayDeque<>(milliSecondsInConsideration / sensorSamplingPeriodInMillis);
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
        Log.i(TAG, "IdleChecker Activate.");
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
        sensorManager.registerListener(sensorEventListener, sensor, sensorSamplingPeriodInMillis * 1000);
        // *1000 beacause samplingPeriodUs uses Microseconds as unit

    }

    public void Deactive(){
        Log.i(TAG, "IdleChecker Deactive.");
        sensorManager.unregisterListener(sensorEventListener);
    }
    private void ElaborateAccelerationModule(double accelerationModule) {
        // if queue is full, pop the oldest one before adding the newest
        if(lastAccelerations.size() == milliSecondsInConsideration / sensorSamplingPeriodInMillis){
            sumOfLastAccelerations -= lastAccelerations.remove();
        }
        sumOfLastAccelerations += accelerationModule;
        lastAccelerations.addLast(accelerationModule);

        // IF it's passed more than alertInterval time since lastMovement time, alert and reset lastMovement time.
        if(System.currentTimeMillis() > lastMovement + alertInterval){
            Alert("It has been so long, Heads Up and take a break!");
            lastMovement = System.currentTimeMillis();
        }

        // IF there had been a movement, reset lastMovement time to current time  <-  if the medium of last 5 measurement > 0.2
        if(sumOfLastAccelerations / lastAccelerations.size() > 0.2)
            lastMovement = System.currentTimeMillis();
    }


    private void Alert(String Alertmsg){
            notificationService.sendNotificationToUser(Alertmsg);
    }

    private boolean isPhoneLocked() {
        return keyguardManager.isKeyguardLocked();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
