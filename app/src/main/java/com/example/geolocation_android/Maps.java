package com.example.geolocation_android;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private List<Marker> tmpRealTimeMarker;
    private List<Marker> realTimeMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        realTimeMarker = new ArrayList<>();
        tmpRealTimeMarker = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void countDownTimer(){
        new CountDownTimer(30000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Seconds Remaining",""+millisUntilFinished/1000);

            }

            @Override
            public void onFinish() {
                Toast.makeText(Maps.this,"Actualizando", Toast.LENGTH_SHORT).show();
                onMapReady(mMap);
            }
        }.start();
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

        // Add a marker in Sydney and move the camera
        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(Marker marker:realTimeMarker){
                    marker.remove();
                }
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    MapsServer mp = snapshot.getValue(MapsServer.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud,longitud));
                    tmpRealTimeMarker.add(mMap.addMarker(markerOptions));
                }

                realTimeMarker.clear();
                realTimeMarker.addAll(tmpRealTimeMarker);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
