package com.example.ProgettoAMIF.model.notificationService.statusBarSystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ProgettoAMIF.UI.MainActivity;
import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.interfaces.INotificationService;

public class StatusBarNotification implements INotificationService {

    private static final String TAG = "StatusBarNotification";
    private Context context;
    private String NotificationTitle;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationID;

    public StatusBarNotification(Context context, String notificationTitle) {
        Log.i(TAG, "costrutore StatusBarNotification");
        this.context = context;
        this.NotificationTitle = notificationTitle;

        // get notification builder
        notificationBuilder = new NotificationCompat.Builder(context, context.getText(R.string.channelID).toString() );

        // build notification
        // setting SmallIcon and Title for notification
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(NotificationTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                ;

        // setting the notification's TAP action
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                ;

        // first button  HomeScreen
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent cancelAndGoHome = new Intent(context, CancelNotificationAndGoHomeScreen.class);
        PendingIntent startMainPendingIntent = PendingIntent.getService(context, 10, cancelAndGoHome, 0);
        notificationBuilder
                .addAction(R.mipmap.ic_launcher_round, "Go HomeScreen", startMainPendingIntent)
                ;

    }

    @Override
    public void sendNotificationToUser(String msg) {
        // setting the text of notification
        notificationBuilder.setContentText(msg);

        // ID can be used in future if you want to update or remove it
        notificationID = 1;
        NotificationManagerCompat.from(context).notify(notificationID, notificationBuilder.build());
    }
}
