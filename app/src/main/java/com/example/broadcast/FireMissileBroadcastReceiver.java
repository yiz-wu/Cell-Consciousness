package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.interfaces.OnFireMissile;

public class FireMissileBroadcastReceiver extends BroadcastReceiver {

    private OnFireMissile onFireMissile = null;

    public FireMissileBroadcastReceiver(OnFireMissile ofm){
        onFireMissile = ofm;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onFireMissile.fire();
    }
}
