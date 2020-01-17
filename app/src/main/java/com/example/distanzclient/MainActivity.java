package com.example.distanzclient;

import androidx.appcompat.app.AppCompatActivity;

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
    TextView ausgabe;
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
    }
    private void initGui(){
        startBtn = findViewById(R.id.startBtn);
        ausgabe = findViewById(R.id.ausgabe);
        ausgabe.setMovementMethod(new ScrollingMovementMethod());
        distanzEingabe = findViewById(R.id.distanzEingabe);
        startBtn.setOnClickListener(x->{
            Long distanz = Long.parseLong(distanzEingabe.getText().toString());
            ausgabe.append("App je : "+distanz+ "m, holt GPS Fix\n");
            Toast.makeText(this.getApplicationContext(),"App je : "+distanz+ "m, holt GPS Fix",Toast.LENGTH_SHORT).show();
            CostumLocationManager.updateLocationPeriod(this,distanz);
        });
    }
}
