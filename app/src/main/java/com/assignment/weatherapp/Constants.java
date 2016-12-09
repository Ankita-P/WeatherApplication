package com.assignment.weatherapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankitapatil on 12/8/16.
 */

public class Constants {

    public static final String API_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String API_IMAGEURL = "http://openweathermap.org/img/w/";
    public static final String API_KEY = "cd549ccbdeb4fd0ab66faa0028272e2d";

    public static ServerCall serverCall = new ServerCall();

    public static String CurrentTime = null;
    public static Long CityID;
    public static String CurrentCity = null;
    public static String CurrentCountry = null;
    public static String CurrentTemperature = null;
    public static String CurrentWinds = null;
    public static String CurrentPressure = null;
    public static String CurrentHumidity = null;
    public static String CurrentForecast = null;

    public static ArrayList<String> List_Date = new ArrayList<String>();
    public static ArrayList<String> List_Day = new ArrayList<String>();
    public static ArrayList<String> List_MinTemperature = new ArrayList<String>();
    public static ArrayList<String> List_MaxTemperature = new ArrayList<String>();
    public static ArrayList<String> List_WeatherIcon = new ArrayList<String>();
    public static List<String> List_CityName = new ArrayList<String>();
    public static ArrayList<Long> List_CityID = new ArrayList<Long>();


    public static void ClearWeatherData()
    {
        CurrentTime = null;
        CurrentTemperature = null;
        CurrentCountry = null;
        CurrentWinds = null;
        CurrentPressure = null;
        CurrentHumidity = null;
        CurrentForecast = null;

        List_Date.clear();
        List_Day.clear();
        List_MaxTemperature.clear();
        List_MinTemperature.clear();
        List_WeatherIcon.clear();
    }

    public static void ClearCityData()
    {
        List_CityName.clear();
        List_CityID.clear();
    }
}