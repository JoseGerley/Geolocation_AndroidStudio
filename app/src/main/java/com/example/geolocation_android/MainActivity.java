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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseReference database;
    private Button btnMaps,btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(this);
        database = FirebaseDatabase.getInstance().getReference();
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        getLatLongFirebase();
    }

    private void getLatLongFirebase() {
        int perFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int perCoarse = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if(perFine != PackageManager.PERMISSION_GRANTED && perCoarse != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnMaps:
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.btnAdd:
                Intent intent2 = new Intent(MainActivity.this,formularioMarcador.class);
                startActivity(intent2);
                break;
        }

    }
}
