package com.example.ProgettoAMIF.model.detectors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.ProgettoAMIF.interfaces.ITouchDetector;
import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.model.FasciaOrariaExecutor;

public class TouchDetectService extends Service implements ITouchDetector {

    public static boolean running = false;
    private final static String TAG = "TouchDetectService";
    WindowManager windowManager;
    LinearLayout detectorView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "starting TouchDetectService...");

        TouchDetectService.running = true;
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        detectorView = new LinearLayout(getApplicationContext());

        LayoutParams params = new LayoutParams(1, LayoutParams.MATCH_PARENT);
        detectorView.setLayoutParams(params);
        detectorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch of TouchDetectorService :" + event.toString());
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
                    onTouchDetected();
                return false;
            }
        });
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new LayoutParams(
                1, /* width */
                1, /* height */
                LAYOUT_FLAG,
                LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(detectorView, params);

        Log.i(TAG, "TouchDetectService started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTouchDetected() {

        Intent intent = new Intent(this, FasciaOrariaExecutor.class);
        intent.putExtra(this.getString(R.string.TouchDetected), "a");
        startService(intent);

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy TouchDetectService...");
        windowManager.removeView(detectorView);
        TouchDetectService.running = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
