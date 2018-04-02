package com.example.sameh.sensordatatest;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sameh on 3/26/2018.
 */

public class Trip {

    private int tripId;
    private int truck_id;
    private ArrayList<LatLng> locations;
    private LatLng source;
    private LatLng destination;

    public Trip(int tripId, int truck_id, ArrayList<LatLng> locations, LatLng source, LatLng destination) {
        this.tripId = tripId;
        this.truck_id = truck_id;
        this.locations = locations;
        this.source = source;
        this.destination = destination;
    }

    public Trip(){
        locations = new ArrayList<>();
    }
    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public void setTruck_id(int truck_id) {
        this.truck_id = truck_id;
    }

    public void setLocations(ArrayList<LatLng> locations) {
        this.locations = locations;
    }

    public void setSource(LatLng source) {
        this.source = source;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public int getTripId() {
        return tripId;
    }

    public int getTruck_id() {
        return truck_id;
    }

    public ArrayList<LatLng> getLocations() {
        return locations;
    }

    public LatLng getSource() {
        return source;
    }

    public LatLng getDestination() {
        return destination;
    }
}
