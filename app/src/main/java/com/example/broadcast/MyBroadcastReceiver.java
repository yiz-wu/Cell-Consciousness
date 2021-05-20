package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dialogFragment.FireMissilesDialogFragment;
import com.example.eserciziobroadcastreceiver.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    final String TAG = "MyBroadcastReceiver";

    public MyBroadcastReceiver() {
        // praticamente vuota
        Log.i(TAG, "Costruttore MyBroadcastReceiver()");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // context contiene l'interfaccia grafica che ha lanciato intento
        Log.i(TAG, "onReceive()");

        String msg = intent.getStringExtra(context.getString(R.string.MSG_STRING));

        // verificare la correttezza del messaggio
        Toast.makeText(context, "Messaggio : "+msg, Toast.LENGTH_LONG).show();

    }
}
