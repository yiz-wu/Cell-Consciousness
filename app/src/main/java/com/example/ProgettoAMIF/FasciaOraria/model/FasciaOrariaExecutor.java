package com.example.ProgettoAMIF.FasciaOraria.model;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ProgettoAMIF.UI.MainActivity;
import com.example.ProgettoAMIF.FasciaOraria.data.FasciaOraria;
import com.example.ProgettoAMIF.interfaces.IAccelerometerDetector;
import com.example.ProgettoAMIF.interfaces.IFasciaOrariaExecutor;
import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.interfaces.IScreenDetector;
import com.example.ProgettoAMIF.interfaces.ITouchDetector;
import com.example.ProgettoAMIF.FasciaOraria.model.detectors.ScreenStateReceiver;
import com.example.ProgettoAMIF.notificationService.ToastAndStatusBarNotification;
import com.example.ProgettoAMIF.notificationService.ToastNotification;
import com.example.ProgettoAMIF.notificationService.dialogAlertSystem.DialogNotification;
import com.example.eserciziobroadcastreceiver.R;

public class FasciaOrariaExecutor extends Service implements IFasciaOrariaExecutor {

    private static final String TAG = "FasciaOrariaExecutor";
    private ScreenStateReceiver screenStateReceiver;
    private IScreenDetector screenDetector;
    private ITouchDetector touchDetector;
    private IAccelerometerDetector accelerometerDetector;
    private INotificationService notificationService;

    boolean screen, accelerometer, touch = false;

    int secondiPermessi;

    private CountDownTimer countDownTimer1 = null;

    private long lastScreenLockTime = 0;
    private final int tolleranzaInMinute = 2 * 60;
    private boolean countDownTimerIsRunning = false;
    private int notificationType;

    @Override
    public void turnOnDetectors() {
        Log.i(TAG, "turnOnDetectors.");

        // register screenDetector (Broadcast Receiver)
        Log.i(TAG, "turnOnDetectors : screenDetector");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        if(screenStateReceiver == null)
            screenStateReceiver = new ScreenStateReceiver(this);
        try{
            unregisterReceiver( screenStateReceiver);
        } catch (IllegalArgumentException e) {  }
        registerReceiver(screenStateReceiver, intentFilter);


        //activate Accelerometer (Broadcast Receiver)
//        Log.i(TAG, "turnOnDetectors : accelerometerDetector");
//        if(accelerometerDetector == null)
//            accelerometerDetector = new AccelerometerDetector(this);

        // activate TouchDetectService
//        Log.i(TAG, "turnOnDetectors : touchDetector");
//        if(!TouchDetectService.running){
//            Intent intent = new Intent(this, TouchDetectService.class);
//            startService(intent);
//        }

    }

    @Override
    public void turnOffDetectors() {
        try{
            // unregister screenDetector (Broadcast Receiver)
            unregisterReceiver(screenStateReceiver);

            // unregister Accelerometer (Broadcast Receiver)
//            if(accelerometerDetector != null)
//                ((AccelerometerDetector) accelerometerDetector).end();

        } catch(IllegalArgumentException e){    }


        // deactivate TouchDetectService
//        Intent intent = new Intent(this, TouchDetectService.class);
//        stopService(intent);

//        screen = touch = accelerometer = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy.");
        turnOffDetectors();
        if(countDownTimer1 != null)
            countDownTimer1.cancel();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received Intent in onStartCommand.");


        // se l'intento vuole disattivare Service
        String stop = intent.getStringExtra("stop");
        if(stop != null){
            Log.i(TAG, "Received start Intent : contains STOP");
            if(countDownTimer1 != null)
                countDownTimer1.cancel();
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }


        // se l'intento viene da ScreenStateReceiver
        String screenState = intent.getStringExtra(this.getString(R.string.ScreenState));
        if(screenState != null){
            Log.i(TAG, "Received Intent from ScreenStateReceiver :" + screenState);

            // se messaggio e' BLOCCO : segno il tempo del blocco di cell per confrontarlo nel futuro
            if(screenState == Intent.ACTION_SCREEN_OFF){
                lastScreenLockTime = System.currentTimeMillis();
                return super.onStartCommand(intent, flags, startId);
            }

            // se messaggio e' SBLOCCO : inizia countdown se non ancora iniziato
            if(countDownTimer1 == null || !countDownTimerIsRunning)
                startCountDown();
            return super.onStartCommand(intent, flags, startId);
        }

//        if(intent.getStringExtra(this.getString(R.string.AccelerometerChanged)) != null){
//            Log.i(TAG, "Received start Intent : contains AccelerometerChanged");
//            // fai qualcosa
//            accelerometer = true;
//            makeDecision();
//            return super.onStartCommand(intent, flags, startId);
//        }

//        if(intent.getStringExtra(this.getString(R.string.TouchDetected)) != null){
//            Log.i(TAG, "Received start Intent : contains TouchDetected");
//            // segniamo il tempo
//            lastTouch = System.currentTimeMillis();
//            return super.onStartCommand(intent, flags, startId);
//        }


        // se l'intento vuole attivare Service
        Log.i(TAG, "starting service.");
        // associo service ad una notifica -> ForegroundService
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new NotificationCompat.Builder(this, getText(R.string.channelID).toString())
                        .setContentTitle("Cellphone usage warder")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setNotificationSilent()
                        .build();
        startForeground(13, notification);

        initialization(intent);
        turnOnDetectors();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initialization(Intent intent) {
        Log.i(TAG, "Initialization");

        secondiPermessi = intent.getIntExtra("SecondiPermessi", 2);
        notificationType = intent.getIntExtra("TipoNotifica", 0);
        switch (notificationType){
            case FasciaOraria.NOTIFICATION:
                notificationService = new ToastAndStatusBarNotification(this, intent.getStringExtra("Name"));
                break;
            case FasciaOraria.DIALOG:
                Intent start = new Intent(this, DialogNotification.class);
                startService(start);
//                notificationService = new DialogNotification(this, intent.getStringExtra("Name"));
                break;
            default:
                notificationService = new ToastNotification(this);
                break;
        }
        Log.i(TAG, "notificationType is " + notificationType);

        // se nel momento d'inizio, lo schermo e' gia acceso -> utente sta usando cell,   inizia countdown
        if(!isPhoneLocked(getApplicationContext())){
            startCountDown();
        }
    }

    private void startCountDown() {
        Log.i(TAG, "startCountDown");
        countDownTimer1 = new CountDownTimer(secondiPermessi  * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "onTick : " + millisUntilFinished);
                countDownTimerIsRunning = true;
            }
            @Override
            public void onFinish() {
                Log.i(TAG, "CountDown onFinish");
                countDownTimerIsRunning = false;
                // se alla termine del countdown, l'utente sta ancora usando cell, notifica
                if (!isPhoneLocked(getApplicationContext())) {
                    Log.i(TAG, "Phone is unlocked : " + lastScreenLockTime + ", " + (System.currentTimeMillis() - 1000*tolleranzaInMinute));
                    // se l'utente sta riprendendo cell dopo 2 minuti di tolleranza, riparte countdown
                    if(lastScreenLockTime!=0 && lastScreenLockTime <= System.currentTimeMillis() - 1000*tolleranzaInMinute){
                        Log.i(TAG, "Restart CountDown");
                        startCountDown();
                        return;
                    }
                    // altrimenti mando notifica
                    Log.i(TAG, "send notification");
                    if(notificationType == FasciaOraria.DIALOG){
                        Intent i = new Intent(getApplicationContext(), DialogNotification.class);
                        i.putExtra("msg", "Time out!!! Leave cellphone and Take a brake!");
                        startService(i);
                    } else {
                        notificationService.sendNotificationToUser("Time out!!! Leave cellphone and Take a brake!");
                    }
                    countDownTimer1.start();
                }
            }
        };
        countDownTimer1.start();
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

//    public void makeDecision(){
//        Log.i(TAG, "Inside makeDecision : screen="+screen+" touch="+touch+" acc="+accelerometer);
//
//        // decidere / controllare se utente sta giocando cell da troppo tempo
//        if(screen && touch && accelerometer){
//            notificationService.sendNotificationToUser("WEIYO");
//            screen = touch = accelerometer = false;
//        }
//
//    }

}
