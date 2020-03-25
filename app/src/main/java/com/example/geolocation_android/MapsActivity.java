package com.example.geolocation_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private LocationManager ubicacion;
    DatabaseReference database;
    private Marker actualPosition;
    private boolean primero = true;
    private TextView txtUbicacion;
    private Button btnUbicarme;
    private List<Marcadores> marcadores;
    double latitud, longitud, dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dist = Double.MAX_VALUE;
        database = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        txtUbicacion = findViewById(R.id.txtUbicacion);
        btnUbicarme = findViewById(R.id.btnUbicarme);
        marcadores= new ArrayList<>();
        btnUbicarme.setOnClickListener(this);
        generateLocation();
        regisLocation();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (actualPosition != null)
                    actualPosition.remove();


                MapsServer mp = dataSnapshot.getValue(MapsServer.class);
                MarkerOptions markerOptions = new MarkerOptions();
                latitud = mp.getLatitud();
                longitud = mp.getLongitud();

                LatLng coordenadas = new LatLng(latitud, longitud);
                String ubi = "direccion no encontrada";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    Address dir = geocoder.getFromLocation(latitud,longitud,1).get(0);
                    ubi = dir.getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                actualPosition = mMap.addMarker(new MarkerOptions().position(coordenadas)
                        .title(ubi));

                if(primero){
                    CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas,18);
                    mMap.animateCamera(miUbicacion);
                    primero = false;

                }

                Log.e("Intervencio", "Previa");
                for(Marcadores aux : marcadores){
                    Log.e("Intervencio", "Media1");

                    Double latAux = aux.getLatitud();
                    Double lonAux = aux.getLongitud();
                    Location auxMarkers = new Location("puntoMarker");
                    auxMarkers.setLatitude(latAux);
                    auxMarkers.setLongitude(lonAux);
                    Location user = new Location("puntoUsuario");
                    user.setLatitude(latitud);
                    user.setLongitude(longitud);
                    double dif = user.distanceTo(auxMarkers);
                    Log.e("Diferencia", ""+dif);

                    if(dif < dist){
                        dist = dif;
                        String douCon = String.format("%.2f",dist);
                        txtUbicacion.setText("Punto mas cercano: "+"\n"+ aux.getNombre()+" - "+aux.getDireccion()+"\n"+"Se encuentra a "+douCon+"mts");
                    }
                    Log.e("Intervencio", "Media2");

                }
                Log.e("Intervencio", "Posterior");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("marcadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Check","Markerts");
                if(!marcadores.isEmpty())
                    marcadores.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Marcadores mp = snapshot.getValue(Marcadores.class);
                    marcadores.add(mp);

                    MarkerOptions markerOptions = new MarkerOptions();
                    Double lat = mp.getLatitud();
                    Double lon = mp.getLongitud();
                    LatLng coordenadas = new LatLng(lat, lon);

                    mMap.addMarker(new MarkerOptions().position(coordenadas)
                            .title("" + mp.getNombre() + " - " + mp.getDireccion()));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                LatLng coordenadas = new LatLng(latitud, longitud);
                CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas,20);
                mMap.animateCamera(miUbicacion);
            }
        });



    }

    private void generateLocation() {
        int perFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int perCoarse = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if(perFine != PackageManager.PERMISSION_GRANTED && perCoarse != PackageManager.PERMISSION_GRANTED){
            return;
        }
        ubicacion = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(ubicacion!=null){
            Log.e("Latitud: ", location.getLatitude() + " - Longitud: " + location.getLongitude());
            Map<String, Object> latlong = new HashMap<>();
            latlong.put("latitud", location.getLatitude());
            latlong.put("longitud", location.getLongitude());
            database.child("usuarios").setValue(latlong);
        }

        LatLng coordenadas = new LatLng(location.getLatitude(),location.getLongitude());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUbicarme:
                LatLng coordenadas = new LatLng(latitud, longitud);
                CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas,18);
                mMap.animateCamera(miUbicacion);
                break;
        }
    }

    private void regisLocation(){
        int perFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int perCoarse = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if(perFine != PackageManager.PERMISSION_GRANTED && perCoarse != PackageManager.PERMISSION_GRANTED){
            return;
        }
        ubicacion =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ubicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,new miLocalizacionListener() );
    }



    private class miLocalizacionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Map<String, Object> latlong = new HashMap<>();
            latlong.put("latitud", location.getLatitude());
            latlong.put("longitud", location.getLongitude());
            database.child("usuarios").setValue(latlong);


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
