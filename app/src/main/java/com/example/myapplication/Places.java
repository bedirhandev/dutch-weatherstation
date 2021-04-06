package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Places implements Serializable {
    private List<Place> places;

    public Places(List<Place> places) {
        this.places = places;
    }

    public Place getPlace(String query) {
        for (Place place : places) {
            if (place.getName().equalsIgnoreCase(query)) {
                return place;
            }
        }
        return null;
    }

    public List<Place> getPlaces() {
        return this.places;
    }
    public void setPlaces(List<Place> places) { this.places = places; }

    public List<String> getCities() {
        List<String> cities = new ArrayList();

        for (Place place : places) {
            cities.add(place.getName());
        }

        return cities;
    }
}
