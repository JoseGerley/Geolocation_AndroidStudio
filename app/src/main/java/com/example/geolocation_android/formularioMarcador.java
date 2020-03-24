package com.example.geolocation_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class formularioMarcador extends AppCompatActivity implements View.OnClickListener{

    private Button btnAdd,btnback;
    private EditText lat,lon,name;
    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_marcador);
        btnAdd = findViewById(R.id.btnAgregar);
        btnback = findViewById(R.id.btnVolver);
        lat = findViewById(R.id.etLatitud);
        lon = findViewById(R.id.etLongitud);

        name = findViewById(R.id.etNombre);
        btnback.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVolver:
                Intent intent = new Intent(formularioMarcador.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnAgregar:
                Map<String, Object> item= new HashMap<>();
                double latitud = Double.parseDouble(lat.getText().toString());
                double longitud = Double.parseDouble(lon.getText().toString());
                item.put("latitud", latitud);
                item.put("longitud", longitud);
                item.put("nombre", name.getText().toString());
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    Address dir = geocoder.getFromLocation(latitud,longitud,1).get(0);
                    item.put("direccion", dir.getAddressLine(0));
                } catch (IOException e) {
                    item.put("direccion", "Not find location");
                    e.printStackTrace();
                }


                lat.setText("");
                lon.setText("");
                name.setText("");


                database.child("marcadores").push().setValue(item);
                break;
        }
    }
}
