package com.example.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements OverviewFragment.OnClickListener {

    private Places places = new Places(new ArrayList<>());
    String INTERNAL_FILE_NAME = "";
    String ASSET_FILE_NAME = "";
    boolean needUpdate = false;

    private void writeToFile(String filename, String data) throws IOException {
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        //Log.d("INFO", getFileStreamPath(filename).getPath());
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        osw.write(data);
        osw.close();
    }

    private String readFromFile(String filename) throws IOException {
        String res = "";
        InputStream is = null;

        try {
            is = openFileInput(filename);
        } catch (FileNotFoundException e) {
            return res;
        }

        if(is != null) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String tmp = "";
            StringBuilder sb = new StringBuilder();
            while((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }

            is.close();
            res = sb.toString();
        }

        return res;
    }

    private String JsonDataFromAsset(String fileName, String charType) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int sizeOfFile = is.available();
            byte[] bufferData = new byte[sizeOfFile];
            is.read(bufferData);
            is.close();
            json = new String(bufferData,charType);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(json);
        try {
            writeToFile("dutch_cities.json", data);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return json;
    }

    private boolean getPlacesFromAssetFile()
    {
        JSONObject jsonObject = null;
        String data = JsonDataFromAsset(ASSET_FILE_NAME, "UTF-8");

        try {
            jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("places");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityData = jsonArray.getJSONObject(i);
                String placeName = cityData.getString("name");
                Place place = new Place(placeName);
                places.getPlaces().add(place);
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    private boolean getPlacesFromCache() {
        JSONObject jsonObject = null;

        try {
            String data = readFromFile(INTERNAL_FILE_NAME);


            jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("places");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityData = jsonArray.getJSONObject(i);
                String placeName = cityData.getString("name");
                String placeTemp = cityData.getString("temp");
                String pressure = cityData.getString("pressure");
                String humidity = cityData.getString("humidity");
                Place place = new Place(placeName, placeTemp, pressure, humidity);
                places.getPlaces().add(place);
            }
        } catch (JSONException | IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("places", places);
    }

    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        INTERNAL_FILE_NAME = getString(R.string.dutch_weather_report_filename);
        ASSET_FILE_NAME = getString(R.string.dutch_cities_filename);

        if(places.getPlaces().size() == 0) {
            if(savedInstanceState != null) {
                places = (Places) savedInstanceState.getSerializable("places");
            } else {
                if(!getPlacesFromCache()) {
                    getPlacesFromAssetFile();
                    needUpdate = true;
                }
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        OverviewFragment overviewFragment;

        if(isPortrait())
        {
            overviewFragment = new OverviewFragment();
            overviewFragment.setArguments(new Bundle());
            overviewFragment.getArguments().putSerializable("places", places);
            fragmentManager.beginTransaction().replace(R.id.containerFragment, overviewFragment, "overview").addToBackStack(null).commit();
        } else {
            overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentById(R.id.overviewFragment);
            overviewFragment.setArguments(new Bundle());
            overviewFragment.getArguments().putSerializable("places", places);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

                Place place = places.getPlace(query);

                if(place != null) {
                    if (portrait) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        DetailFragment detailFragment = new DetailFragment();
                        detailFragment.setArguments(new Bundle());
                        detailFragment.getArguments().putSerializable("place", place);
                        fragmentManager.beginTransaction().replace(R.id.containerFragment, detailFragment, "detail").addToBackStack(null).commit();
                    } else {
                        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
                        detailFragment.setPlace(place);
                    }
                }

                searchView.setQuery("", false);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.toolbar_refresh)
        {
            boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            OverviewFragment overviewFragment;
            if(portrait) {
                overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentByTag("overview");
            } else {
                overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentById(R.id.overviewFragment);
            }
            overviewFragment.updateWeatherDataFromAllPlaces(places);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Place place) {
        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if(portrait) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("place", place);
            detailFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.containerFragment, detailFragment, "detail").addToBackStack(null).commit();
        } else {
            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.detailFragment);
            detailFragment.setPlace(place);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(places);
        try {
            writeToFile(INTERNAL_FILE_NAME, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemsUpdate(Places places) {
        // Considering to refactor
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(places);
        try {
            writeToFile(INTERNAL_FILE_NAME, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReady() {
        if(needUpdate) {
            OverviewFragment overviewFragment;

            if(isPortrait())
            {
                overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentByTag("overview");
            } else {
                overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentById(R.id.overviewFragment);
            }

            overviewFragment.updateWeatherDataFromAllPlaces(places);
        }
    }
}