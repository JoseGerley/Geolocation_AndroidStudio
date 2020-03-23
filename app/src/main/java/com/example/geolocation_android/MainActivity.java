package com.example.geolocation_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE_ASK_PERMISSION=111;
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference database;
    private Button btnMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        database = FirebaseDatabase.getInstance().getReference();

        getLatLongFirebase();
    }

    private void getLatLongFirebase() {
        int perFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int perCoarse = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if(perFine != PackageManager.PERMISSION_GRANTED && perCoarse != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_ASK_PERMISSION);

        }
        Log.e("Prueba:", "Muestra de funcionamiento");
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.e("Latitud: ", location.getLatitude() + " - Longitud: " + location.getLongitude());
                            Map<String, Object> latlong = new HashMap<>();
                            latlong.put("latitud", location.getLatitude());
                            latlong.put("longitud", location.getLongitude());
                            database.child("usuarios").push().setValue(latlong);
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnMaps:
                Intent intent = new Intent(MainActivity.this,Maps.class);
                startActivity(intent);
                break;
        }
    }
}
