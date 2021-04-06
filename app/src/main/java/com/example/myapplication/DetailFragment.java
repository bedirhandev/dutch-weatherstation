package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    TextView placeNameView, placeTempView, placePressureView, placeHumidityView;

    public DetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        placeNameView = view.findViewById(R.id.placeNameView);
        placeTempView = view.findViewById(R.id.placeTempView);
        placePressureView = view.findViewById(R.id.placeAirPressureView);
        placeHumidityView = view.findViewById(R.id.placeHumdityView);

        if(getArguments() != null)
        {
            Place place = (Place)getArguments().getSerializable("place");
            setPlace(place);
        }

        return view;
    }

    public void setPlace(Place place) {
        placeNameView.setText("Plaats: " + place.getName());
        if(!place.getTemp().trim().equals("\u2103")) placeTempView.setText("Temperatuur: " + place.getTemp());
        if(!place.getPressure().trim().equals("hPa")) placePressureView.setText("Luchtdruk: " + place.getPressure());
        if(!place.getHumidity().trim().equals("%")) placeHumidityView.setText("Luchtvochtigheid: " + place.getHumidity());
    }
}