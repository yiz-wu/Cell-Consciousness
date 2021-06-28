package com.example.ProgettoAMIF.reminders;

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
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.notificationService.ToastNotification;
import com.example.eserciziobroadcastreceiver.R;

import java.util.ArrayDeque;

public class MovementChecker extends Service{

    private static final String TAG = "MovementChecker";
    public static final String AlertMsg = "Head Up, Phone Down!";
    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private KeyguardManager keyguardManager;
    private INotificationService notificationService;

    private final int sensorSamplingPeriodInMillis = 500;   // measure every 0.5 seconds
    private final int milliSecondsInConsideration = 5000;   // consider last 5 seconds measurement
    private long lastAlertTimeStamp = 0;
    private final int alertIntervall = 5000; // 5 seconds
    private double sumOfLastAccelerations = 0;
    private ArrayDeque<Double> lastAccelerations = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "MovementChecker onStartCommand.");
        context = this;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, getText(R.string.channelID).toString())
                        .setContentTitle("Movement Checker")
                        .setContentText("Movement Checker in work.")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setNotificationSilent()
                        .build();
        // Notification ID cannot be 0.
        // associate this service with a notification so it will become a Foreground Service
        startForeground(11, notification);

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
        Log.i(TAG, "MovementChecker Initialization.");
        notificationService = new ToastNotification(context);
        lastAccelerations = new ArrayDeque<>(milliSecondsInConsideration / sensorSamplingPeriodInMillis);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

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
        Log.i(TAG, "MovementChecker Activate.");
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
        sensorManager.registerListener(sensorEventListener, sensor, sensorSamplingPeriodInMillis * 1000);
        // *1000 beacause samplingPeriodUs uses Microseconds as unit

    }

    public void Deactive(){
        Log.i(TAG, "MovementChecker Deactive.");
        sensorManager.unregisterListener(sensorEventListener);
    }


    private void ElaborateTotalAcceleration(double accelerationModule) {
        // I'm considering only last 5 seconds's movement, that will be 10 sensor measurement
        if(lastAccelerations.size() == milliSecondsInConsideration / sensorSamplingPeriodInMillis){
            sumOfLastAccelerations -= lastAccelerations.remove();
        }
        sumOfLastAccelerations += accelerationModule;
        lastAccelerations.addLast(accelerationModule);
        Log.i(TAG, "sumOfLastAccelerations : " + sumOfLastAccelerations);

        // with test, I found out that event when walking slowly, the medium is above 1.2
        if( lastAccelerations.size() == milliSecondsInConsideration / sensorSamplingPeriodInMillis
            && sumOfLastAccelerations / lastAccelerations.size() > 1.2)
            Alert(AlertMsg);
    }


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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
