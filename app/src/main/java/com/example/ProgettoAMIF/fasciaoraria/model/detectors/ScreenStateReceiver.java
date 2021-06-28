package com.example.ProgettoAMIF.fasciaoraria.model.detectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.fasciaoraria.model.FasciaOrariaExecutor;

public class ScreenStateReceiver extends BroadcastReceiver{

    private static final String TAG = "ScreenStateReceiver";
    private Context context;

    public ScreenStateReceiver(Context context) {
        super();
        Log.i(TAG, "Costruttore ScreenOnOffBroadcastReceiver()");
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent : "+intent.toString());
        onScreenDetected(intent.getAction());
    }

    public void onScreenDetected(String action) {
        Intent intent = new Intent(context, FasciaOrariaExecutor.class);
        intent.putExtra(context.getString(R.string.ScreenState), action);
        context.startService(intent);
    }
}
