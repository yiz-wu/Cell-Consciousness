package com.example.ProgettoAMIF.model.notificationService.dialogAlertSystem;

import android.content.Context;
import android.content.Intent;

import com.example.ProgettoAMIF.interfaces.INotificationService;

public class DialogNotification implements INotificationService {

    private Context context;
    private String dialogTitle;

    public DialogNotification(Context context, String dialogTitle) {
        this.context = context;
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void sendNotificationToUser(String msg) {
        Intent i = new Intent(context, TransparentActivity.class);
        i.putExtra("title", dialogTitle);
        i.putExtra("msg", msg);
        context.startActivity(i);
    }


}
