package com.example.ProgettoAMIF.notificationService.dialogAlertSystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.interfaces.IAlertDialogListener;

public class DialogActivity extends AppCompatActivity implements IAlertDialogListener {

    private static final String TAG = "DialogActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        Log.i(TAG, "onCreate()");

        new AlertDialogFragment(getIntent().getStringExtra("msg"), getIntent().getStringExtra("title")).show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void onFinishDialog() {
        Log.i(TAG, "onFinishDialog()");
        finish();
    }
}