package com.example.ProgettoAMIF.model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.ProgettoAMIF.data.FasciaOraria;
import com.example.ProgettoAMIF.interfaces.IAccelerometerDetector;
import com.example.ProgettoAMIF.interfaces.IFasciaOrariaExecutor;
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.interfaces.IScreenDetector;
import com.example.ProgettoAMIF.interfaces.ITouchDetector;
import com.example.ProgettoAMIF.model.detectors.AccelerometerDetector;
import com.example.ProgettoAMIF.model.detectors.ScreenOnOffBroadcastReceiver;
import com.example.ProgettoAMIF.model.detectors.TouchDetectService;
import com.example.ProgettoAMIF.model.notificationService.ToastNotification;
import com.example.ProgettoAMIF.model.notificationService.dialogAlertSystem.DialogNotification;
import com.example.ProgettoAMIF.model.notificationService.statusBarSystem.StatusBarNotification;
import com.example.eserciziobroadcastreceiver.R;

public class FasciaOrariaExecutor extends Service implements IFasciaOrariaExecutor {

    private IScreenDetector screenDetector;
    private ITouchDetector touchDetector;
    private IAccelerometerDetector accelerometerDetector;
    private INotificationService notificationService;

    boolean screen;
    boolean accelerometer;
    boolean touch;


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

            touch = true;

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.ScreenUnlocked)) != null){
            // fai qualcosa

            screen = true;

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.AccelerometerChanged)) != null){
            // fai qualcosa

            accelerometer = true;

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

        if(screen && touch && accelerometer){
            notificationService.sendNotificationToUser("WEIYO");
            screen = touch = accelerometer = false;
        }

    }


}
