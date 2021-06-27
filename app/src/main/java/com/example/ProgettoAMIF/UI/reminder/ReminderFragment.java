package com.example.ProgettoAMIF.UI.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.ProgettoAMIF.model.IdleChecker;
import com.example.ProgettoAMIF.model.LightChecker;
import com.example.ProgettoAMIF.model.MovementChecker;
import com.example.ProgettoAMIF.model.StopRemindersReceiver;
import com.example.eserciziobroadcastreceiver.R;

import java.util.Calendar;
import java.util.Date;

public class ReminderFragment extends Fragment {

    public static final String LIGHT_KEY = "light";
    public static final String MOVEMENT_KEY = "movement";
    public static final String IDLE_KEY = "idle";
    public static final String STOPHOUR_KEY = "STOPHOUR_KEY";
    public static final String STOPMINUTE_KEY = "STOPMINUTE_KEY";
    public static final String RESTARTHOUR_KEY = "RESTARTHOUR_KEY";
    public static final String RESTARTMINUTE_KEY = "RESTARTMINUTE_KEY";
    private final String TAG = "ReminderFragment";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private View root, lyLight, lyMovement, lyIdle;
    private SwitchCompat switchLight, switchMovement, switchIdle;

    private View lySpiegazione;
    private Button closeSpiegazione;
    private TextView spiegazioneTitolo;
    private TextView spiegazioneDescrizione;
    private ImageView spiegazioneImmagine;

    private TextView reminderStopTime;
    private int stopHour, stopMinute = 0;
    private Button applyChange;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reminder, container, false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        sharedPref = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        initFunctionLayout();
        initReminderStopTime();
        initSpiegazioneLayout();
    }


    private void initFunctionLayout() {
        lyLight = root.findViewById(R.id.lyLight);
        lyMovement = root.findViewById(R.id.lyMovement);
        lyIdle = root.findViewById(R.id.lyIdle);
        switchLight = root.findViewById(R.id.switchLight);
        switchMovement = root.findViewById(R.id.switchMovement);
        switchIdle = root.findViewById(R.id.switchIdle);

        checkSwitches();

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
                Intent intent = new Intent(context, LightChecker.class);
                if(isChecked){
                    Toast.makeText(getContext(),"Brightness Checker Activated", Toast.LENGTH_SHORT ).show();
                    context.startService(intent);
                } else {
                    Toast.makeText(getContext(),"Brightness Checker Deactivated", Toast.LENGTH_SHORT ).show();
                    context.stopService(intent);
                }
                editor.putBoolean(LIGHT_KEY, isChecked);
                editor.apply();
            }
        });
        switchMovement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(context, MovementChecker.class);
                if(isChecked){
                    Toast.makeText(getContext(),"Movement Checker Activated", Toast.LENGTH_SHORT ).show();
                    context.startService(intent);
                } else {
                    Toast.makeText(getContext(),"Movement Checker Deactivated", Toast.LENGTH_SHORT ).show();
                    context.stopService(intent);
                }
                editor.putBoolean(MOVEMENT_KEY, isChecked);
                editor.apply();
            }
        });
        switchIdle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(context, IdleChecker.class);
                if(isChecked){
                    Toast.makeText(getContext(),"Idle Alerter Activated", Toast.LENGTH_SHORT ).show();
                    context.startService(intent);
                } else {
                    Toast.makeText(getContext(),"Idle Alerter Deactivated", Toast.LENGTH_SHORT ).show();
                    context.stopService(intent);
                }
                editor.putBoolean(IDLE_KEY, isChecked);
                editor.apply();
            }
        });
    }

    private void initReminderStopTime() {
        reminderStopTime = root.findViewById(R.id.reminderStopTime);
        applyChange = root.findViewById(R.id.applyChange);

        stopHour = sharedPref.getInt(STOPHOUR_KEY, 22);
        stopMinute = sharedPref.getInt(STOPMINUTE_KEY, 0);
        reminderStopTime.setText(stopHour+":"+stopMinute);



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

        applyChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putInt(STOPHOUR_KEY, stopHour);
                editor.putInt(STOPMINUTE_KEY, stopMinute);
                editor.commit();

                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, StopRemindersReceiver.class);
                intent.putExtra("stop", true);
                PendingIntent stopIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                // stop time has already passed -> stop reminders right now
                if(stopHour<calendar.get(Calendar.HOUR_OF_DAY) || (stopHour==calendar.get(Calendar.HOUR_OF_DAY) && stopMinute <= calendar.get(Calendar.MINUTE))){
                    startActivity(intent);
                } else { // otherwise set the alarm to send intent
                    calendar.set(Calendar.HOUR_OF_DAY, stopHour);
                    calendar.set(Calendar.MINUTE, stopMinute);
                    calendar.set(Calendar.SECOND, 0);
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), stopIntent);
                    Log.i(TAG, "setExact alarm for " + calendar.getTime());
                }

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

    private void checkSwitches() {
        if(sharedPref.getBoolean(LIGHT_KEY, false)){
            switchLight.setChecked(true);
        }
        if(sharedPref.getBoolean(MOVEMENT_KEY, false)){
            switchMovement.setChecked(true);
        }
        if(sharedPref.getBoolean(IDLE_KEY, false)){
            switchIdle.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        checkSwitches();
    }

}