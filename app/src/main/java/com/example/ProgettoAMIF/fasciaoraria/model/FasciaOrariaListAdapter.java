package com.example.ProgettoAMIF.fasciaoraria.model;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;


import com.example.ProgettoAMIF.fasciaoraria.data.FasciaOraria;
import com.example.ProgettoAMIF.fasciaoraria.data.FasciaOrariaHandler;
import com.example.eserciziobroadcastreceiver.R;

import java.util.ArrayList;

public class FasciaOrariaListAdapter extends ArrayAdapter<FasciaOraria> {

    private final String TAG = "FasciaOrariaListAdapter";
    private final Context context;
    private final int resource;
    private FasciaOrariaHandler fasciaOrariaHandler;

    public FasciaOrariaListAdapter(Context context, int resource, ArrayList<FasciaOraria> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public FasciaOrariaListAdapter(Context context, int resource, ArrayList<FasciaOraria> objects, FasciaOrariaHandler fasciaOrariaHandler) {
        this(context,resource,objects);
        this.fasciaOrariaHandler = fasciaOrariaHandler;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FasciaOraria currentFasciaOraria = getItem(position);
        int ID = currentFasciaOraria.getID();
        String name = currentFasciaOraria.getName();
        int startHour = currentFasciaOraria.getStartHour();
        int startMinute = currentFasciaOraria.getStartMinute();
        int endHour = currentFasciaOraria.getEndHour();
        int endMinute = currentFasciaOraria.getEndMinute();
        int secondiPermessi = currentFasciaOraria.getSecondiPermessi();
        boolean active = currentFasciaOraria.isActive();
        int notificationType = currentFasciaOraria.getNotificationType();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        View popUp = convertView.findViewById(R.id.popUp);
        TextView tvFasciaOrariaName = convertView.findViewById(R.id.tvFasciaOrariaName);
        TextView tvFasciaOrariaDetail = convertView.findViewById(R.id.tvFasciaOrariaDetail);
        SwitchCompat switchFasciaOraria = convertView.findViewById(R.id.switchFasciaOraria);
        Button delete = convertView.findViewById(R.id.delete);
        Button edit = convertView.findViewById(R.id.edit);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick ");
                if(popUp.getVisibility() == View.VISIBLE)
                    popUp.setVisibility(View.GONE);
                else
                    popUp.setVisibility(View.VISIBLE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fasciaOrariaHandler.deleteFasciaOrariaByID(getItem(position).getID());
                notifyDataSetChanged();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View editView = inflater.inflate(R.layout.edit_fasciaoraria, null, false);

                EditText etName = editView.findViewById(R.id.etName);
                etName.setText(name);

                TimePicker startTimePicker = editView.findViewById(R.id.startTime);
                TimePicker endTimePicker = editView.findViewById(R.id.endTime);
                startTimePicker.setIs24HourView(true);
                endTimePicker.setIs24HourView(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startTimePicker.setHour(startHour);
                    startTimePicker.setMinute(startMinute);
                    endTimePicker.setHour(endHour);
                    endTimePicker.setMinute(endMinute);
                } else {
                    startTimePicker.setCurrentHour(startHour);
                    startTimePicker.setCurrentMinute(startMinute);
                    endTimePicker.setCurrentHour(endHour);
                    endTimePicker.setCurrentMinute(endMinute);
                }

                NumberPicker allowedSecondsPicker = editView.findViewById(R.id.secondiPermessi);
                allowedSecondsPicker.setMinValue(0);
                allowedSecondsPicker.setMaxValue(1500);
                allowedSecondsPicker.setValue(secondiPermessi);

                RadioGroup notificationTypeGroup = editView.findViewById(R.id.notificationGroup);
                switch (notificationType){
                    case FasciaOraria.NOTIFICATION:
                        notificationTypeGroup.check(R.id.notification);
                        break;
                    case FasciaOraria.DIALOG:
                        notificationTypeGroup.check(R.id.alert);
                        break;
                    default:
                        notificationTypeGroup.check(R.id.toast);
                        break;
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(editView);
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                        currentFasciaOraria.setName(etName.getText().toString());
                        currentFasciaOraria.setStartHour(startTimePicker.getCurrentHour());
                        currentFasciaOraria.setStartMinute(startTimePicker.getCurrentMinute());
                        currentFasciaOraria.setEndHour(endTimePicker.getCurrentHour());
                        currentFasciaOraria.setEndMinute(endTimePicker.getCurrentMinute());
                        currentFasciaOraria.setSecondiPermessi(allowedSecondsPicker.getValue());
                        int notificationType;
                        switch (notificationTypeGroup.getCheckedRadioButtonId()){
                            case R.id.alert:
                                notificationType = FasciaOraria.DIALOG;
                                break;
                            case R.id.notification:
                                notificationType = FasciaOraria.NOTIFICATION;
                                break;
                            default:
                                notificationType = FasciaOraria.TOAST;
                                break;
                        }
                        Log.i(TAG, "onClick of " + name + " notifcationType is " + notificationType);
                        currentFasciaOraria.setNotificationType(notificationType);
                        switchFasciaOraria.setChecked(false);
                        notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

        switchFasciaOraria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean previousState = active;
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == previousState)
                    return;
                Log.i(TAG, "onCheckedChanged of "+name+" : " + isChecked);
                if(isChecked){
                    fasciaOrariaHandler.enableFasciaOraria(ID);
                    Toast.makeText(context, "Activated!", Toast.LENGTH_SHORT).show();
                } else {
                    fasciaOrariaHandler.disableFasciaOraria(ID);
                    Toast.makeText(context, "Deactivated!", Toast.LENGTH_SHORT).show();
                }
                currentFasciaOraria.setActive(isChecked);
                previousState = isChecked;
            }
        });

        tvFasciaOrariaName.setText(name);
        String tipoNotifica;
        switch (getItem(position).getNotificationType()){
            case FasciaOraria.NOTIFICATION:
                tipoNotifica = "Notification";
                break;
            case FasciaOraria.DIALOG:
                tipoNotifica = "Alert Dialog";
                break;
            default:
                tipoNotifica = "Toast";
                break;
        }
        String details = String.format("%02d:%02d - %02d:%02d  |  %d seconds  |  %s",startHour, startMinute, endHour, endMinute, secondiPermessi, tipoNotifica);
        tvFasciaOrariaDetail.setText(details);
        switchFasciaOraria.setChecked(active);

        return convertView;
    }

}
