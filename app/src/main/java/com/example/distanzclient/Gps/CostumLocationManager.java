package com.example.distanzclient.Gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.distanzclient.BerichtigungsManager;
import com.example.distanzclient.Rest.RestManager;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CostumLocationManager {
    public static LocationManager locManager;
    public static TextView ausgabe;
    public static float maxSpeedEingabe;
    public static long distanzEingabe;
    public static LocationListener distanzLis, distanzSpeedLis;
    public static AppCompatActivity activityG;
    static Location result;
    public static Location costumLastLocation() {

        getFusedLocationProviderClient(activityG).getLastLocation().addOnSuccessListener(activityG, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    result = location;
                    if(location.getSpeed() > maxSpeedEingabe) {
                        ausgabe.append("LocationNr."+RestManager.counter+"- speed ist over : " + maxSpeedEingabe);
                        return;
                    }
                    else{
                        ausgabe.append("LocationNr ."+RestManager.counter+"- speed was over : " + maxSpeedEingabe);
                        updateLocationDistanzSpeed();
                    }
                    Toast.makeText(activityG.getApplicationContext(), "Location : " + location.getLatitude() + "\n" + location.getLongitude(), Toast.LENGTH_LONG);
                }
                Log.e("pos", "onSuccess: Location : " + location.getLatitude() + "\n" + location.getLongitude());
            }
        });
        return result;
    }

    public static void updateLocationDistanzSpeed() {
        if(distanzSpeedLis == null){
            RestManager.counter =0;
            distanzSpeedLis = new CostumLocationListenerD();
        }
        if(distanzLis != null){
            locManager.removeUpdates(distanzLis);
            locManager.removeUpdates(distanzSpeedLis);
        }
        if (activityG.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activityG.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else {
            CostumLocationManager.locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distanzEingabe, distanzSpeedLis, Looper.myLooper());
        }
    }

    public static CostumLocation mapToCostum(Location location) {
        CostumLocation l = new CostumLocation();
        l.altitude = new Double(location.getAltitude()).longValue();
        l.timestamp = location.getTime();
        l.latitude = location.getLatitude();
        l.longitude = location.getLongitude();
        l.teamid = 25;
        l.trackid = RestManager.trackid;
        l.session = "test Session from kamil";
        l.counter = RestManager.counter;
        return l;
    }

    public static void updateLocationDistanz(){
        if(distanzLis == null){
            RestManager.counter = 0;
            distanzLis = new CostumLocationListener();
        }
        if(distanzSpeedLis != null){
            locManager.removeUpdates(distanzLis);
            locManager.removeUpdates(distanzSpeedLis);
        }
        if (CostumLocationManager.locManager.isLocationEnabled()) {
                if (activityG.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activityG.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }else {
                    CostumLocationManager.locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distanzEingabe, distanzLis, Looper.myLooper());
                    Toast.makeText(activityG,"ein Location sent",Toast.LENGTH_SHORT);
                }
        }
    }

    public static class CostumLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            RestManager.postLocation(CostumLocationManager.mapToCostum(location));
            ausgabe.append("location von onChanged with speed : "+location.getSpeed()+"\n");
            Log.e("location post",location.getAltitude() +location.getLongitude()+"");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
 public static class CostumLocationListenerD implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location.getSpeed() > maxSpeedEingabe){
                locManager.removeUpdates(distanzSpeedLis);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CostumLocationManager.costumLastLocation();
                    }
                }, 10000);
            }
            else {
                ausgabe.append("LocationNr."+RestManager.counter+"location von onChanged with speed : " + (location.getSpeed() / 1000) * 3600 + "\n");
                Log.e("location post", location.getAltitude() + location.getLongitude() + "");
                RestManager.postLocation(CostumLocationManager.mapToCostum(location));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }


}
