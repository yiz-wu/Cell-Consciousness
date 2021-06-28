package com.example.ProgettoAMIF.fasciaoraria.model.detectors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ProgettoAMIF.interfaces.IAccelerometerDetector;
import com.example.eserciziobroadcastreceiver.R;
import com.example.ProgettoAMIF.fasciaoraria.model.FasciaOrariaExecutor;

public class AccelerometerDetector implements SensorEventListener , IAccelerometerDetector {

    private static final String TAG = "AccelerometerDetector";
    private SensorManager sensorManager = null;
    private SensorEventListener sensorEventListener = null;
    private Sensor accelerometer = null;

    private Context context;

    public AccelerometerDetector(Context context) {
        Log.i(TAG, "Costruttore AccelerometerDetector()");
        this.context = context;

        sensorEventListener = this;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer == null){
            Toast.makeText(context.getApplicationContext(), "Accelerometer not valid.", Toast.LENGTH_LONG);
            Log.e(TAG, "Accelerometer not valid.");
            return;
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void end(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(filtro(event))
           onAccelerometerDetected();
    }

    private boolean filtro(SensorEvent event){
        // analizza event

        // se la variazione e' inferiore alla sensibilita' dell'accelerometro
        return false;

        // se effettivamente c'e' stato un spostamento
//        return true;
    }

    @Override
    public void onAccelerometerDetected() {
        Intent intent = new Intent(context, FasciaOrariaExecutor.class);
        intent.putExtra(context.getString(R.string.AccelerometerChanged), "a");
        context.startService(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

}
