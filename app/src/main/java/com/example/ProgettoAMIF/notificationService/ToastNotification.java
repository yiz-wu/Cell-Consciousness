package com.example.ProgettoAMIF.notificationService;

import android.content.Context;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.INotificationService;

public class ToastNotification implements INotificationService {
    private Context context;

    public ToastNotification (Context context){
        this.context = context;
    }

    @Override
    public void sendNotificationToUser(String msg) {
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }
}
