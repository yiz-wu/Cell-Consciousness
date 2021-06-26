package com.example.ProgettoAMIF.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ProgettoAMIF.model.IdleChecker;
import com.example.ProgettoAMIF.model.LightChecker;
import com.example.ProgettoAMIF.model.MovementChecker;
import com.example.ProgettoAMIF.model.detectors.ScreenUnlockBroadcastReceiver;
import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.model.notificationService.statusBarSystem.StatusBarNotification;
import com.example.ProgettoAMIF.model.notificationService.dialogAlertSystem.TransparentActivity;


public class MainActivity2 extends AppCompatActivity{

    private static final String TAG = "MainActivity2";
    TextView tvMsg = null;
    EditText etMsg = null;
    Button bSEND = null;
    Button bASK = null;
    Button bNOTIFICATION = null;
    Button bSTART = null;
    Button bSTOP = null;

    BroadcastReceiver myReceiver = null;
    BroadcastReceiver screenReceiver = null;
    Context context;

    StatusBarNotification notificationService;
    LightChecker lightChecker;
    MovementChecker movementChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;

        tvMsg = findViewById(R.id.tvMsg);
        etMsg = findViewById(R.id.etMsg);
        bSEND = findViewById(R.id.bSEND);
        bASK = findViewById(R.id.bASK);
        bNOTIFICATION = findViewById(R.id.bNOTIFICATION);
        bSTART = findViewById(R.id.bSTART);
        bSTOP = findViewById(R.id.bSTOP);

//        initScreenReceiver();
        initPermissionButton();
        initNotificationButton();

        createChannelID();


        tvMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch of tvMsg");
                // return true if the event was handled -> finish here, do not propagate
                // false otherwise
                return false;
            }
        });


        bSTART.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick of START button");
//                Intent intent = new Intent(context, FasciaOrariaExecutor.class);
//                intent.putExtra("Name", "TextName");
//                intent.putExtra("TipoNotifica", 0);
//                startService(intent);
//                Log.i(TAG, "onClick of START button : intent sent.");


//                Intent intent = new Intent(context, LightChecker.class);
//                startService(intent);
//
//                Intent intent2 = new Intent(context, MovementChecker.class);
//                startService(intent2);

                Intent intent3 = new Intent(context, IdleChecker.class);
                startService(intent3);

            }
        });

        bSTOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick of STOP button");
//                Intent intent = new Intent(context, FasciaOrariaExecutor.class);
//                intent.putExtra("stop", "stop");
//                startService(intent);
//                Log.i(TAG, "onClick of STOP button : intent sent.");

//                Intent intent = new Intent(context, LightChecker.class);
//                stopService(intent);
//
//                Intent intent2 = new Intent(context, MovementChecker.class);
//                stopService(intent2);

                Intent intent3 = new Intent(context, IdleChecker.class);
                stopService(intent3);

            }
        });

//        DialogNotification dialogNotification = new DialogNotification(this, "SALUTO");
//        dialogNotification.sendNotificationToUser("oh ciao bella");

//        notificationService = new StatusBarNotification(this, "ciao");
//        notificationService.sendNotificationToUser("AOHD");

    }

    private void createChannelID() {
        // for Android 8.0 and higher, u must register your app's notification channel before sending notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            String description = "My channel's description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(getText(R.string.channelID).toString(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initNotificationButton() {
        bNOTIFICATION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "inside NotificationButton ClickListener");

                String CHANNEL_ID = "MyChannelID";
                NotificationCompat.Builder notificationBuilder;
                // for Android 8.0 and higher, u must register your app's notification channel before sending notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "MyChannel";
                    String description = "My channel's description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                    Log.i(TAG, "inside channel");
                }


                String msg = "This is a notification msg";
                notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);

                notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Notification Title")
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                ;

                // setting the notification's TAP action
                // context that created the intent,   what you want to do/open with this intent
                Intent intent = new Intent(MainActivity2.this, MainActivity2.class);
                intent.putExtra("Msg","You can put data inside intent, so the called activities can get it");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                notificationBuilder
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        ;


                Intent dialogIntent = new Intent(context, TransparentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent dialogPendingIntent = PendingIntent.getActivity(context, 10, dialogIntent, 0);

                notificationBuilder
                        .addAction(R.mipmap.ic_launcher_round, "FIRE MISSILE", dialogPendingIntent);



                // or send a broadcast with a Button
                Intent _i = new Intent(getString(R.string.my_special_action));
                _i.putExtra(getString(R.string.MSG_STRING), "oh ciao");

                PendingIntent actionIntent = PendingIntent.getBroadcast(MainActivity2.this, 0, _i, 0);

                // put the pendingIntent inside the notification
                // so when user tap it, it invoke the send() method of intent
                // in our case, this intent opens the MainActivity2
                notificationBuilder
                        .addAction(R.mipmap.ic_launcher, "SEND TOAST", actionIntent)
                        ;


                // ID can be used in future if you want to update or remove it
                int notificationID = 1;


                Log.i(TAG, "before notifying");
                NotificationManagerCompat.from(MainActivity2.this).notify(notificationID, notificationBuilder.build());
                Log.i(TAG, "after notifying");
            }
        });

    }

    private void initPermissionButton() {
        bASK.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // redirect to setting page where user can turn on this permission
                if (!Settings.canDrawOverlays(MainActivity2.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 0);
                } else {
                    Toast.makeText(MainActivity2.this, "Draw Overlays permission already granted", Toast.LENGTH_SHORT).show();
                }

                // ask for camera permission
//                askPermission(Manifest.permission.CAMERA, 1);
                askPermission(Manifest.permission.FOREGROUND_SERVICE, 2);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // se termino senza deregistrare il broadcastreceiver, android lancia un errore
        unregisterReceiver(myReceiver);
        unregisterReceiver(screenReceiver);
    }



    private void initScreenReceiver() {
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);

        screenReceiver = new ScreenUnlockBroadcastReceiver(this);
        registerReceiver(screenReceiver, screenFilter);

    }


    public void askPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(MainActivity2.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity2.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity2.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity2.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity2.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity2.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity2.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);
//
//        switch (action) {
//            case (MotionEvent.ACTION_DOWN):
//                Log.i(TAG, "onTouchEvent : Action was DOWN");
//                return true;    // return false   if you want ignore this motionevent in future, so it's MOVE and UP will not be caught
//            case (MotionEvent.ACTION_MOVE):
//                Log.i(TAG, "Action was MOVE");
//                return true;
//            case (MotionEvent.ACTION_UP):
//                Log.i(TAG, "Action was UP");
//                return true;
//            case (MotionEvent.ACTION_CANCEL):
//                Log.i(TAG, "Action was CANCEL");
//                return true;
//            case (MotionEvent.ACTION_OUTSIDE):
//                Log.i(TAG, "Movement occurred outside bounds " +
//                        "of current screen element");
//                return true;
//            default:
//                return super.onTouchEvent(event);
//        }
//    }

}