package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataTask extends AsyncTask<List<Place>, List<Place>, List<Place>> {

    private Context context;
    private String API_KEY = "";
    private List<Place> _places = new ArrayList();

    public interface AsyncResponse {
        void processFinish(List<Place> result);
    }

    public AsyncResponse delegate = null;

    public WeatherDataTask(Context context, AsyncResponse asyncResponse)
    {
        this.delegate = asyncResponse;
        this.context = context;
    }

    @Override
    protected List<Place> doInBackground(List<Place>... places) {
        RequestQueue queue = Volley.newRequestQueue(context);

        API_KEY = context.getString(R.string.openweathermap_api_key);

        for(int position = 0; position < places[0].size(); position++)
        {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    places[0].get(position).getName() +
                    ",nl&appid=" + API_KEY + "&units=metric";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String name = response.getString("name");
                        String temp = response.getJSONObject("main").getString("temp");
                        String pressure = response.getJSONObject("main").getString("pressure");
                        String humidity = response.getJSONObject("main").getString("humidity");

                        Place place = new Place(name, temp, pressure, humidity);
                        _places.add(place);
                    } catch (JSONException e) {
                        return;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    return;
                }
            });

            queue.add(jsonObjectRequest);
        }

        while(_places.size() != places[0].size()) {}

        return _places;
    }

    protected void onPostExecute(List<Place> result) {
        delegate.processFinish(result);
    }

}
