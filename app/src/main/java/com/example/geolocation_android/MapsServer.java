package com.example.geolocation_android;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsServer {
    private double latitud;
    private double longitud;

    public MapsServer(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

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

    public Map<String, Object> toHash(){
        Map<String, Object> result = new HashMap<>();
        result.put("latitud",latitud);
        result.put("longitud",longitud);
        return result;
    }
}
