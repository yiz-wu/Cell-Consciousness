package com.example.ProgettoAMIF.model.notificationService.dialogAlertSystem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.ProgettoAMIF.interfaces.INotificationService;

public class DialogNotification extends Service implements INotificationService {

    private final static String TAG = "DialogNotification";
    WindowManager windowManager;
    LinearLayout detectorView;
    private Context context;
    private String dialogTitle;

//    public DialogNotification(Context context, String dialogTitle) {
//        this.context = context;
//        this.dialogTitle = dialogTitle;
//    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        this.context = this;
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        detectorView = new LinearLayout(getApplicationContext());

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        detectorView.setLayoutParams(params);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(detectorView, params);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        this.context = this;
        if(intent.hasExtra("msg")){
            Intent i = new Intent(context, DialogActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("title", dialogTitle);
            i.putExtra("msg", intent.getStringExtra("msg"));
            context.startActivity(i);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void sendNotificationToUser(String msg) {
        Intent i = new Intent(context, DialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("title", dialogTitle);
        i.putExtra("msg", msg);
        context.startActivity(i);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {        return null;    }
}
