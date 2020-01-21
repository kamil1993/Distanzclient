package com.example.distanzclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Range;

import com.example.distanzclient.Gps.CostumLocation;
import com.example.distanzclient.Gps.CostumLocationManager;

public class SensorListener implements SensorEventListener {
    int counter = 0;
    int size = 100;
    float[] zeds = new float[size];
    Range<Float> pos = Range.create(9.6f, 10.0f);
    Range<Float> neg = Range.create(-10.0f, -9.6f);
    boolean noMotion = false;


    @Override
    public void onSensorChanged(SensorEvent event) {

       if ((pos.contains(event.values[2]) || neg.contains(event.values[2])) && counter < size) {
            zeds[counter] = event.values[2];
            counter++;
            return;
        }
        if (counter < size) {
            if(counter>0){
                counter--;
                zeds[counter] = 0;
            }
            if (CostumLocationManager.activityG.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && CostumLocationManager.activityG.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if(noMotion){
                CostumLocationManager.locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, CostumLocationManager.distanzEingabe, CostumLocationManager.distanzSpeedLis, Looper.myLooper());
                noMotion = false;
                return;
            }

        }
        counter = 0;
        if(noMotion(zeds) && CostumLocationManager.distanzSpeedLis != null){
            CostumLocationManager.locManager.removeUpdates(CostumLocationManager.distanzSpeedLis);
            noMotion = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean noMotion(float [] arr){
        float sum = 0;
        for(int i = 0; i<arr.length;i++){
            if(arr[i]<0){
                arr[i] = -1 * arr[i];
            }
            sum += arr[i];
        }
        return pos.contains(sum/size);
    }
}
