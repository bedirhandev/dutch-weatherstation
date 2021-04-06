package com.example.myapplication;

import java.io.Serializable;

public class Place implements Serializable, Comparable<Place> {

    private String id;
    private String name;
    private String temp;
    private String pressure;
    private String humidity;

    public Place(String name, String temp, String pressure, String humidity) {
        this.name = name;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public Place(String name) {
        this.name =   name;
        this.temp = "";
        this.pressure = "";
        this.humidity = "";
    }

    public void setId(String id)
    {
        this.id = id;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setPressure(String pressure)
    {
        this.pressure = pressure;
    }
    public void setHumidity(String humidity)
    {
        this.humidity = humidity;
    }
    public void setTemp(String temp)
    {
        this.temp = temp;
    }

    public String getId()
    {
        return this.id;
    }
    public String getName() { return this.name; }
    public String getPressure()
    {
        return this.pressure + " hPa";
    }
    public String getHumidity()
    {
        return this.humidity + "%";
    }
    public String getTemp()
    {
        return this.temp + " \u2103";
    }

    @Override
    public int compareTo(Place o) {
        return this.name.compareTo(o.name);
    }
}
