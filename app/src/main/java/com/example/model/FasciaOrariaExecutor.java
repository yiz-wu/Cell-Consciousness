package com.example.model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.method.Touch;

import androidx.annotation.Nullable;

import com.example.broadcast.ScreenOnOffBroadcastReceiver;
import com.example.data.FasciaOraria;
import com.example.detectors.AccelerometerDetector;
import com.example.eserciziobroadcastreceiver.R;
import com.example.interfaces.IAccelerometerDetector;
import com.example.interfaces.IFasciaOrariaExecutor;
import com.example.interfaces.INotificationService;
import com.example.interfaces.IScreenDetector;
import com.example.interfaces.ITouchDetector;
import com.example.interfaces.IOnScreenReceived;
import com.example.notificationService.DialogNotification;
import com.example.notificationService.StatusBarNotification;
import com.example.notificationService.ToastNotification;
import com.example.services.TouchDetectService;

public class FasciaOrariaExecutor extends Service implements IFasciaOrariaExecutor {

    private IScreenDetector screenDetector;
    private ITouchDetector touchDetector;
    private IAccelerometerDetector accelerometerDetector;
    private INotificationService notificationService;

    @Override
    public void turnOnDetectors() {
        // register screenDetector (Broadcast Receiver)
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_UNLOCKED);
        screenDetector = new ScreenOnOffBroadcastReceiver(this);
        registerReceiver((BroadcastReceiver) screenDetector, intentFilter);

        // activate TouchDetectService
        Intent intent = new Intent(this, TouchDetectService.class);
        startService(intent);

        //activate Accelerometer (Broadcast Receiver)
        accelerometerDetector = new AccelerometerDetector(this);
    }

    @Override
    public void turnOffDetectors() {
        // unregister screenDetector (Broadcast Receiver)
        unregisterReceiver((BroadcastReceiver) screenDetector);

        // deactivate TouchDetectService
        Intent intent = new Intent(this, TouchDetectService.class);
        stopService(intent);

        // unregister Accelerometer (Broadcast Receiver)
        ((AccelerometerDetector) accelerometerDetector).end();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        turnOffDetectors();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        turnOnDetectors();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String stop = intent.getStringExtra("stop");
        if(stop != null){
            turnOffDetectors();
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.TouchDetected)) != null){
            // fai qualcosa

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.ScreenUnlocked)) != null){
            // fai qualcosa

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.AccelerometerChanged)) != null){
            // fai qualcosa

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }


        int notificationType = intent.getIntExtra("TipoNotifica", 0);
        switch (notificationType){
            case FasciaOraria.NOTIFICATION:
                notificationService = new StatusBarNotification(this, intent.getStringExtra("Name"));
                break;
            case FasciaOraria.DIALOG:
                notificationService = new DialogNotification(this, intent.getStringExtra("Name"));
                break;
            default:
                notificationService = new ToastNotification(this);
                break;
        }

        turnOnDetectors();

        return super.onStartCommand(intent, flags, startId);
    }


    public void makeDecision(){
        // decidere / controllare se utente sta giocando cell da troppo tempo


        notificationService.sendNotificationToUser("WEIYO");
    }


}
