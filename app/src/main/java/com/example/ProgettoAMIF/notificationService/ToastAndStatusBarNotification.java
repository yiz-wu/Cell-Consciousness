package com.example.ProgettoAMIF.notificationService;

import android.content.Context;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.INotificationService;
import com.example.ProgettoAMIF.notificationService.statusBarSystem.StatusBarNotification;

public class ToastAndStatusBarNotification implements INotificationService {

    private StatusBarNotification statusBarNotification;
    private Context context;

    private String statusBarTitle;

    public ToastAndStatusBarNotification(Context context, String statusBarTitle){
        this.context = context;
        this.statusBarTitle = statusBarTitle;

        statusBarNotification = new StatusBarNotification(context, statusBarTitle);
    }

    @Override
    public void sendNotificationToUser(String msg) {
        // Toast Alert
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        // Status Bar Alert
        statusBarNotification.sendNotificationToUser(msg);
    }
}
