package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OverviewFragment extends Fragment implements PlaceListAdapter.OnPlaceListener {
    private RecyclerView recyclerView;
    private PlaceListAdapter arrayAdapter = null;
    private RequestQueue queue;
    private String TAG = "QUEUE";
    //private final int SIZE = 60;
    private String API_KEY = "";

    interface OnClickListener
    {
        void onItemSelected(Place place);
        void onItemsUpdate(Places places);
        void onReady();
    }

    OnClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (OnClickListener) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listener = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        API_KEY = getContext().getString(R.string.openweathermap_api_key);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if(getArguments() != null)
        {
            Places places = (Places) getArguments().getSerializable("places");
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
            recyclerView.addItemDecoration(mDividerItemDecoration);
            recyclerView.setLayoutManager(mLayoutManager);
            arrayAdapter = new PlaceListAdapter(places.getPlaces(), this);
            recyclerView.setAdapter(arrayAdapter);
        }

        if(listener != null) {
            listener.onReady();
        }

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void updateWeatherDataFromAllPlaces(Places places)
    {
        new WeatherDataTask(getContext(), new WeatherDataTask.AsyncResponse(){
            @Override
            public void processFinish(List<Place> result) {
                int size = places.getPlaces().size();
                arrayAdapter.notifyItemRangeRemoved(0, size);
                places.getPlaces().clear();
                places.getPlaces().addAll(result);
                Collections.sort(places.getPlaces(), (o1, o2) -> o1.getName().compareTo(o2.getName()));

                if(listener != null) {
                    listener.onItemsUpdate(places);
                }

                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Task has been completed successfully.", Toast.LENGTH_SHORT).show();
            }
        }).execute(places.getPlaces());
    }

    @Override
    public void onPlaceClick(int position) {
        Place place = arrayAdapter.getPlace(position);

        queue = Volley.newRequestQueue(getContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" +
                place.getName() +
                ",nl&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    place.setTemp(response.getJSONObject("main").getString("temp"));
                    place.setPressure(response.getJSONObject("main").getString("pressure"));
                    place.setHumidity(response.getJSONObject("main").getString("humidity"));

                    // Listener of the MainActivity only needs signal on response is received
                    if (listener != null) {
                        listener.onItemSelected(place);
                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                //Log.d("ERROR", error.getMessage());
                return;
            }
        });

        queue.add(jsonObjectRequest);
    }
}