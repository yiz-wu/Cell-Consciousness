package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.eserciziobroadcastreceiver.R;
import com.example.interfaces.IScreenDetector;
import com.example.interfaces.IOnScreenReceived;
import com.example.model.FasciaOrariaExecutor;

public class ScreenOnOffBroadcastReceiver extends BroadcastReceiver implements IScreenDetector {

    private static final String TAG = "ScreenBroadcastReceiver";
    private Context context;

    public ScreenOnOffBroadcastReceiver(Context context) {
        super();
        Log.i(TAG, "Costruttore ScreenOnOffBroadcastReceiver()");
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onScreenDetected();
    }

    @Override
    public void onScreenDetected() {

        Intent intent = new Intent(context, FasciaOrariaExecutor.class);
        intent.putExtra(context.getString(R.string.ScreenUnlocked), "a");
        context.startService(intent);

    }
}
