package com.example.ProgettoAMIF;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dialogFragment.AlertDialogFragment;
import com.example.dialogFragment.FireMissilesDialogFragment;
import com.example.eserciziobroadcastreceiver.R;
import com.example.interfaces.IAlertDialogListener;

public class TransparentActivity extends AppCompatActivity implements IAlertDialogListener {

    private static final String TAG = "TransparentActivity";

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