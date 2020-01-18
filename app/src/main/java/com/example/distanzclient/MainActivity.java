package com.example.distanzclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.distanzclient.Gps.CostumLocationManager;
import com.example.distanzclient.Rest.RestManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    private LocationManager loc;
    Button startBtn;
    TextView distanzEingabe;
    TextView speedEingabe;
    TextView ausgabe;
    long cSpeed;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BerichtigungsManager.isLocationGranted(this);
        RestManager.ausgabe = findViewById(R.id.ausgabe);
        CostumLocationManager.ausgabe = findViewById(R.id.ausgabe);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        RestManager.trackidInit(this, "first Test aus Periodischen Client", "Test", 25);
        initGui();

        //Sensor
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(new SensorListener(cSpeed), senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void initGui(){
        startBtn = findViewById(R.id.startBtn);
        ausgabe = findViewById(R.id.ausgabe);
        ausgabe.setMovementMethod(new ScrollingMovementMethod());
        distanzEingabe = findViewById(R.id.distanzEingabe);
        speedEingabe = findViewById(R.id.speedEingabe);
        startBtn.setOnClickListener(x->{
            Long distanz = Long.parseLong(distanzEingabe.getText().toString());
            Long speed = Long.parseLong(speedEingabe.getText().toString());
            ausgabe.append("App je : "+distanz+ "m, holt GPS Fix\n");
            Toast.makeText(this.getApplicationContext(),"App je : "+distanz+ "m, holt GPS Fix",Toast.LENGTH_SHORT).show();
            CostumLocationManager.updateLocationPeriod(this,distanz);
        });
    }
}
