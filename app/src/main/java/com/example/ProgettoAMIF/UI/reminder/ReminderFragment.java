package com.example.ProgettoAMIF.UI.reminder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.eserciziobroadcastreceiver.R;

public class ReminderFragment extends Fragment {

    private final String TAG = "ReminderFragment";

    private View root;
    private View lyLight;
    private View lyMovement;
    private View lyIdle;
    private SwitchCompat switchLight;
    private SwitchCompat switchMovement;
    private SwitchCompat switchIdle;

    private View lySpiegazione;
    private Button closeSpiegazione;
    private TextView spiegazioneTitolo;
    private TextView spiegazioneDescrizione;
    private ImageView spiegazioneImmagine;

    private TextView reminderStopTime, reminderRestartTime;
    private int stopHour, stopMinute, restartHour, restartMinute = 0;
    private Button applyChange;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reminder, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        initFunctionLayout();
        initSpiegazioneLayout();
        initReminderWorkingTime();

    }

    private void initReminderWorkingTime() {
        reminderStopTime = root.findViewById(R.id.reminderStopTime);
        reminderRestartTime = root.findViewById(R.id.reminderRestartTime);
        applyChange = root.findViewById(R.id.applyChange);

        // TODO : init stopHour, stopMinute, restartHour, restartMinute from sharedPreferences

        reminderStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick StopTime");
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stopHour = hourOfDay;
                        stopMinute = minute;
                        reminderStopTime.setText(hourOfDay+":"+minute);
                    }
                }, stopHour, stopMinute, true);
                timePickerDialog.updateTime(stopHour, stopMinute);
                timePickerDialog.show();
                applyChange.setClickable(true);
                applyChange.setFocusable(true);
            }
        });
        reminderRestartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick RestartTime");
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        restartHour = hourOfDay;
                        restartMinute = minute;
                        reminderRestartTime.setText(hourOfDay+":"+minute);
                    }
                }, restartHour, restartMinute, true);
                timePickerDialog.updateTime(restartHour, restartMinute);
                timePickerDialog.show();
                applyChange.setClickable(true);
                applyChange.setFocusable(true);
            }
        });
        applyChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : applyChange

                applyChange.setClickable(false);
                applyChange.setFocusable(false);
                Toast.makeText(getContext(),"Change applied", Toast.LENGTH_SHORT ).show();
            }
        });

        applyChange.setClickable(false);
        applyChange.setFocusable(false);
    }

    private void initSpiegazioneLayout() {
        lySpiegazione = root.findViewById(R.id.lySpiegazione);
        closeSpiegazione = root.findViewById(R.id.spiegazione_closeButton);
        spiegazioneTitolo = root.findViewById(R.id.spiegazione_titolo);
        spiegazioneDescrizione = root.findViewById(R.id.spiegazione_descrizione);
        spiegazioneImmagine = root.findViewById(R.id.spiegazione_immagine);
        closeSpiegazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lySpiegazione.setVisibility(View.GONE);
            }
        });
        lySpiegazione.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void initFunctionLayout() {
        lyLight = root.findViewById(R.id.lyLight);
        lyMovement = root.findViewById(R.id.lyMovement);
        lyIdle = root.findViewById(R.id.lyIdle);
        switchLight = root.findViewById(R.id.switchLight);
        switchMovement = root.findViewById(R.id.switchMovement);
        switchIdle = root.findViewById(R.id.switchIdle);

        if(true){
            switchLight.setChecked(false);
        }
        if(true){
            switchMovement.setChecked(false);
        }
        if(true){
            switchIdle.setChecked(false);
        }

        lyLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lySpiegazione.setVisibility(View.VISIBLE);
                spiegazioneTitolo.setText(R.string.brightness);
                spiegazioneDescrizione.setText(R.string.spiegazione_brightness);
                spiegazioneImmagine.setImageResource(R.drawable.ic_brightness);
            }
        });
        lyMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lySpiegazione.setVisibility(View.VISIBLE);
                spiegazioneTitolo.setText(R.string.movement);
                spiegazioneDescrizione.setText(R.string.spiegazione_movement);
                spiegazioneImmagine.setImageResource(R.drawable.ic_movement);
            }
        });
        lyIdle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lySpiegazione.setVisibility(View.VISIBLE);
                spiegazioneTitolo.setText(R.string.idle_alert);
                spiegazioneDescrizione.setText(R.string.spiegazione_idle_alert);
                spiegazioneImmagine.setImageResource(R.drawable.ic_idle);
            }
        });
        switchLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(),"Brightness Checker Activated", Toast.LENGTH_SHORT ).show();

                } else {
                    Toast.makeText(getContext(),"Brightness Checker Deactivated", Toast.LENGTH_SHORT ).show();

                }
            }
        });
        switchMovement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(),"Movement Checker Activated", Toast.LENGTH_SHORT ).show();

                } else {
                    Toast.makeText(getContext(),"Movement Checker Deactivated", Toast.LENGTH_SHORT ).show();

                }
            }
        });
        switchIdle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(),"Idle Alerter Activated", Toast.LENGTH_SHORT ).show();

                } else {
                    Toast.makeText(getContext(),"Idle Alerter Deactivated", Toast.LENGTH_SHORT ).show();

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

}