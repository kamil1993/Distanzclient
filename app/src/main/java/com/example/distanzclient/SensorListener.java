package com.example.distanzclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorListener implements SensorEventListener {
    public long cSpeed;
    public long speed;

    public SensorListener(long cSpeed){
        cSpeed = cSpeed;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
