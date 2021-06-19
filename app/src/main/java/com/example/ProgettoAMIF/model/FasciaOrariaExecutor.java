package com.example.ProgettoAMIF.model;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ProgettoAMIF.data.FasciaOraria;
import com.example.ProgettoAMIF.interfaces.IAccelerometerDetector;
import com.example.ProgettoAMIF.interfaces.IFasciaOrariaExecutor;
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.interfaces.IScreenDetector;
import com.example.ProgettoAMIF.interfaces.ITouchDetector;
import com.example.ProgettoAMIF.model.detectors.AccelerometerDetector;
import com.example.ProgettoAMIF.model.detectors.ScreenUnlockBroadcastReceiver;
import com.example.ProgettoAMIF.model.detectors.TouchDetectService;
import com.example.ProgettoAMIF.model.notificationService.ToastNotification;
import com.example.ProgettoAMIF.model.notificationService.dialogAlertSystem.DialogNotification;
import com.example.ProgettoAMIF.model.notificationService.statusBarSystem.StatusBarNotification;
import com.example.eserciziobroadcastreceiver.R;

import java.sql.Timestamp;

public class FasciaOrariaExecutor extends Service implements IFasciaOrariaExecutor {

    private static final String TAG = "FasciaOrariaExecutor";
    private IScreenDetector screenDetector;
    private ITouchDetector touchDetector;
    private IAccelerometerDetector accelerometerDetector;
    private INotificationService notificationService;

    boolean screen;
    boolean accelerometer;
    boolean touch;

    int minutiPermessi;

    private CountDownTimer countDownTimer1 = null;
    private CountDownTimer countDownTimer2 = null;

    private long lastTouch;


    @Override
    public void turnOnDetectors() {
        Log.i(TAG, "turnOnDetectors.");

        // register screenDetector (Broadcast Receiver)

        Log.i(TAG, "turnOnDetectors : screenDetector");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        if(screenDetector == null)
            screenDetector = new ScreenUnlockBroadcastReceiver(this);
        try{
            unregisterReceiver((BroadcastReceiver) screenDetector);
        } catch (IllegalArgumentException e) {  }
        registerReceiver((BroadcastReceiver) screenDetector, intentFilter);


        //activate Accelerometer (Broadcast Receiver)
        Log.i(TAG, "turnOnDetectors : accelerometerDetector");
        if(accelerometerDetector == null)
            accelerometerDetector = new AccelerometerDetector(this);


        // activate TouchDetectService
        Log.i(TAG, "turnOnDetectors : touchDetector");
        if(!TouchDetectService.running){
            Intent intent = new Intent(this, TouchDetectService.class);
            startService(intent);
        }

    }

    @Override
    public void turnOffDetectors() {
        try{
            // unregister screenDetector (Broadcast Receiver)
            unregisterReceiver((BroadcastReceiver) screenDetector);

            // unregister Accelerometer (Broadcast Receiver)
            if(accelerometerDetector != null)
                ((AccelerometerDetector) accelerometerDetector).end();

        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }


        // deactivate TouchDetectService
        Intent intent = new Intent(this, TouchDetectService.class);
        stopService(intent);

        screen = touch = accelerometer = false;
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
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start Intent.");
        String stop = intent.getStringExtra("stop");
        if(stop != null){
            Log.i(TAG, "Received start Intent : contains STOP");
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }


        if(intent.getStringExtra(this.getString(R.string.ScreenUnlocked)) != null){
            Log.i(TAG, "Received start Intent : contains ScreenUnlocked");
            // fai qualcosa
            countDownTimer1 = new CountDownTimer(minutiPermessi * 60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    // TODO
                    if(!isPhoneLocked(getApplicationContext())){
                        notificationService.sendNotificationToUser("Non è il momento di stare al cellulare");
                    }
                    // se schermo ancora acceso  =>  manda la notifica.
                    // notificationService.sendNotificationToUser("WEIYO");
                }
            };

            return super.onStartCommand(intent, flags, startId);
        }

        if(intent.getStringExtra(this.getString(R.string.AccelerometerChanged)) != null){
            Log.i(TAG, "Received start Intent : contains AccelerometerChanged");
            // fai qualcosa

            accelerometer = true;

            //
            makeDecision();
            return super.onStartCommand(intent, flags, startId);
        }

        if(intent.getStringExtra(this.getString(R.string.TouchDetected)) != null){
            Log.i(TAG, "Received start Intent : contains TouchDetected");

            // segniamo il tempo
            lastTouch = System.currentTimeMillis();

            return super.onStartCommand(intent, flags, startId);
        }


        lastTouch = System.currentTimeMillis();
        minutiPermessi = intent.getIntExtra("MinutiPermessi", 2);
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
        Log.i(TAG, "Received start Intent : contains TipoNotifica = " +notificationType);

        turnOnDetectors();

        return super.onStartCommand(intent, flags, startId);
    }


    public void makeDecision(){
        Log.i(TAG, "Inside makeDecision : screen="+screen+" touch="+touch+" acc="+accelerometer);


        // decidere / controllare se utente sta giocando cell da troppo tempo

        if(screen && touch && accelerometer){
            notificationService.sendNotificationToUser("WEIYO");
            screen = touch = accelerometer = false;
        }

    }
    private boolean isPhoneLocked(Context context) {
        boolean isPhoneLock = false;
        if (context != null) {
            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (myKM != null && myKM.isKeyguardLocked()) {
                isPhoneLock = true;
            }
        }
        return isPhoneLock;
    }

}
