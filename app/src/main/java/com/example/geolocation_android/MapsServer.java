package com.example.geolocation_android;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MapsServer {
    private double latitud;
    private double longitud;

    public MapsServer() {
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
