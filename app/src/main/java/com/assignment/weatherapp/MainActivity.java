package com.assignment.weatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.ContentObservable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private ProgressDialog progress = null;
    private TextView CurrentTime, CurrentCity, CurrentTemperature, CurrentForecast, CurrentWind, CurrentPressure, CurrentHumidity;
    private TextView Day1_MaxTemp, Day1_MinTemp, Day1_Weekday;
    private TextView Day2_MaxTemp, Day2_MinTemp, Day2_Weekday;
    private TextView Day3_MaxTemp, Day3_MinTemp, Day3_Weekday;
    private TextView Day4_MaxTemp, Day4_MinTemp, Day4_Weekday;
    private TextView Day5_MaxTemp, Day5_MinTemp, Day5_Weekday;
    private TextView Day6_MaxTemp, Day6_MinTemp, Day6_Weekday;
    private ImageView Day1_Image, Day2_Image, Day3_Image, Day4_Image, Day5_Image, Day6_Image;
    ImageButton ChangeLocation;
    Bitmap[] bitmapArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        InstantiateUI();

        new GetCurrentWeatherData().execute();

        //Button click event to change activity for changing location.
        ChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(MainActivity.this, SearchCity.class);
                startActivity(changeActivity);
                finish();
            }
        });
    }

    //Asynchronous task to get weather data for 7 days
    private class GetWeatherData extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {

            //UI processing for displaying loader on frontend
            progress.setMessage("Loading");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            super.onPreExecute();
        }

        //Backend processing for fetching data from API
        protected String[] doInBackground(Void... params) {
            try {
                //clear previous data
                Constants.ClearWeatherData();
                String RequestData = Constants.API_URL+"forecast/daily?id="+Constants.CityID+"&cnt=7&APPID="+Constants.API_KEY;

                //sending data to ServerCall class for calling API with requested parameters
                String response = Constants.serverCall.GetWeatherData(RequestData);
                JSONObject response_Object = new JSONObject(response);

                JSONObject CityObject = response_Object.getJSONObject("city");
                Constants.CurrentCity = CityObject.getString("name");
                Constants.CurrentCountry = CityObject.getString("country");

                JSONArray WeatherDataObject = response_Object.getJSONArray("list");

                if (WeatherDataObject.length() > 0) {
                    for (int i = 0; i < WeatherDataObject.length(); i++) {
                        JSONObject WeatherDayObject = WeatherDataObject.getJSONObject(i);

                        long date = WeatherDayObject.getLong("dt");
                        //Converting date from GMT Unix Timestamp to milliseconds
                        date = date * 1000;
                        Date DateObject = new Date(date);
                        SimpleDateFormat dateformatter = new SimpleDateFormat("EEE dd,MMM h:mm:a");
                        String CurrentDate = dateformatter.format(DateObject);

                        String[] DateParts = CurrentDate.split(" ");
                        String day = DateParts[0];
                        String str_date = DateParts[1];
                        String time = DateParts[2];

                        JSONObject TemperatureObject = WeatherDayObject.getJSONObject("temp");
                        double min_temp = TemperatureObject.getDouble("min");
                        //converting Temperature from kelvin to Celcius
                        min_temp -= 273.16;

                        double max_temp = TemperatureObject.getDouble("max");
                        max_temp -= 273.16;

                        JSONArray WeatherDataArray = WeatherDayObject.getJSONArray("weather");
                        JSONObject weatherObject = WeatherDataArray.getJSONObject(0);
                        String forecast = weatherObject.getString("main");
                        String weatherIcon = weatherObject.getString("icon");

                        String url_Str = Constants.API_IMAGEURL+weatherIcon+".png";
                        URL url = new URL(url_Str);
                        bitmapArray[i] = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        //storing data in ArrayLists
                        Constants.List_Date.add(str_date);
                        Constants.List_MinTemperature.add(String.format("%.1f", min_temp));
                        Constants.List_MaxTemperature.add(String.format("%.1f", max_temp));
                        Constants.List_WeatherIcon.add(weatherIcon);
                        Constants.List_Day.add(day);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //UI processing after completion of backend data processing
        @Override
        protected void onPostExecute(String[] result) {

            progress.dismiss();
            DisplayInformation();
            super.onPostExecute(result);
        }
    }

    //Asynchronous task to get current weather data for selected city
    private class GetCurrentWeatherData extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
//UI processing for displaying loader on frontend
            progress.setMessage("Loading");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            super.onPreExecute();
        }

        //Backend processing for fetching data from API
        protected String[] doInBackground(Void... params) {
            try {
                Constants.ClearWeatherData();
                String RequestData = Constants.API_URL+"weather?id="+Constants.CityID+"&APPID="+Constants.API_KEY;
                String response = Constants.serverCall.GetWeatherData(RequestData);
                JSONObject response_Object = new JSONObject(response);

                Constants.CurrentCity = response_Object.getString("name");

                JSONObject SysObject = response_Object.getJSONObject("sys");
                Constants.CurrentCountry = SysObject.getString("country");

                long date = response_Object.getLong("dt");
                //Converting date from GMT Unix Timestamp to milliseconds
                date = date * 1000;
                Date DateObject = new Date(date);
                SimpleDateFormat dateformatter = new SimpleDateFormat("EEE dd,MMM h:mm:a");
                String CurrentDate = dateformatter.format(DateObject);

                String[] DateParts = CurrentDate.split(" ");
                Constants.CurrentTime = DateParts[2];

                JSONArray WeatherDataArray = response_Object.getJSONArray("weather");
                JSONObject weatherObject = WeatherDataArray.getJSONObject(0);
                Constants.CurrentForecast = weatherObject.getString("main");

                JSONObject MainObject = response_Object.getJSONObject("main");
                double currentTemp = MainObject.getDouble("temp");
                //convert Kelvin to Celcius
                currentTemp -= 273.16;
                Constants.CurrentTemperature = String.format("%.1f",currentTemp);
                Constants.CurrentPressure = String.valueOf(MainObject.getDouble("pressure"));
                Constants.CurrentHumidity = String.valueOf(MainObject.getDouble("humidity"));

                JSONObject WindObject = response_Object.getJSONObject("wind");
                Constants.CurrentWinds = String.valueOf(WindObject.getDouble("speed"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //UI processing after completion of backend data processing
        @Override
        protected void onPostExecute(String[] result) {

            progress.dismiss();
            //Displaying data in UI elements
            CurrentTime.setText(Constants.CurrentTime);
            CurrentCity.setText(Constants.CurrentCity+","+Constants.CurrentCountry);
            CurrentTemperature.setText(Constants.CurrentTemperature + "\u2103");
            CurrentForecast.setText(Constants.CurrentForecast);
            CurrentWind.setText(Constants.CurrentWinds+" m/s");
            CurrentPressure.setText(Constants.CurrentPressure+ " hpa");
            CurrentHumidity.setText(Constants.CurrentHumidity+"%");

            new GetWeatherData().execute();
            super.onPostExecute(result);
        }
    }


    //Function to instantiate all UI elements in the layout file
    public void InstantiateUI()
    {

        progress = new ProgressDialog(MainActivity.this);
        bitmapArray = new Bitmap[6];

        ChangeLocation = (ImageButton) findViewById(R.id.ChangeLocationButton);
        CurrentTime = (TextView) findViewById(R.id.Txt_CurrentTime);
        CurrentCity = (TextView) findViewById(R.id.Txt_CityName);
        CurrentTemperature = (TextView) findViewById(R.id.Txt_CurrentTemp);
        CurrentForecast = (TextView) findViewById(R.id.Txt_WeatherCondition);
        CurrentWind = (TextView) findViewById(R.id.Txt_WeatherWind);
        CurrentHumidity = (TextView) findViewById(R.id.Txt_WeatherHumidity);
        CurrentPressure = (TextView) findViewById(R.id.Txt_WeatherPressure);

        Day1_Image = (ImageView) findViewById(R.id.Image_Day1Image);
        Day1_MaxTemp = (TextView) findViewById(R.id.Label_Day1MaxTemp);
        Day1_MinTemp = (TextView) findViewById(R.id.Label_Day1MinTemp);
        Day1_Weekday = (TextView) findViewById(R.id.Label_Day1Day);

        Day2_Image = (ImageView) findViewById(R.id.Image_Day2Image);
        Day2_MaxTemp = (TextView) findViewById(R.id.Label_Day2MaxTemp);
        Day2_MinTemp = (TextView) findViewById(R.id.Label_Day2MinTemp);
        Day2_Weekday = (TextView) findViewById(R.id.Label_Day2Day);

        Day3_Image = (ImageView) findViewById(R.id.Image_Day3Image);
        Day3_MaxTemp = (TextView) findViewById(R.id.Label_Day3MaxTemp);
        Day3_MinTemp = (TextView) findViewById(R.id.Label_Day3MinTemp);
        Day3_Weekday = (TextView) findViewById(R.id.Label_Day3Day);

        Day4_Image = (ImageView) findViewById(R.id.Image_Day4Image);
        Day4_MaxTemp = (TextView) findViewById(R.id.Label_Day4MaxTemp);
        Day4_MinTemp = (TextView) findViewById(R.id.Label_Day4MinTemp);
        Day4_Weekday = (TextView) findViewById(R.id.Label_Day4Day);

        Day5_Image = (ImageView) findViewById(R.id.Image_Day5Image);
        Day5_MaxTemp = (TextView) findViewById(R.id.Label_Day5MaxTemp);
        Day5_MinTemp = (TextView) findViewById(R.id.Label_Day5MinTemp);
        Day5_Weekday = (TextView) findViewById(R.id.Label_Day5Day);

        Day6_Image = (ImageView) findViewById(R.id.Image_Day6Image);
        Day6_MaxTemp = (TextView) findViewById(R.id.Label_Day6MaxTemp);
        Day6_MinTemp = (TextView) findViewById(R.id.Label_Day6MinTemp);
        Day6_Weekday = (TextView) findViewById(R.id.Label_Day6Day);

    }

    //function to display next 6 days weather information in UI elements
    public void DisplayInformation()
    {
        Day1_Image.setImageBitmap(bitmapArray[0]);
        Day1_Weekday.setText(Constants.List_Day.get(0));
        Day1_MinTemp.setText(Constants.List_MinTemperature.get(0)+ "\u2103");
        Day1_MaxTemp.setText(Constants.List_MaxTemperature.get(0)+ "\u2103");

        Day2_Image.setImageBitmap(bitmapArray[1]);
        Day2_Weekday.setText(Constants.List_Day.get(1));
        Day2_MinTemp.setText(Constants.List_MinTemperature.get(1)+ "\u2103");
        Day2_MaxTemp.setText(Constants.List_MaxTemperature.get(1)+ "\u2103");

        Day3_Image.setImageBitmap(bitmapArray[2]);
        Day3_Weekday.setText(Constants.List_Day.get(2));
        Day3_MinTemp.setText(Constants.List_MinTemperature.get(2)+ "\u2103");
        Day3_MaxTemp.setText(Constants.List_MaxTemperature.get(2)+ "\u2103");

        Day4_Image.setImageBitmap(bitmapArray[3]);
        Day4_Weekday.setText(Constants.List_Day.get(3));
        Day4_MinTemp.setText(Constants.List_MinTemperature.get(3)+ "\u2103");
        Day4_MaxTemp.setText(Constants.List_MaxTemperature.get(3)+ "\u2103");

        Day5_Image.setImageBitmap(bitmapArray[4]);
        Day5_Weekday.setText(Constants.List_Day.get(4));
        Day5_MinTemp.setText(Constants.List_MinTemperature.get(4)+ "\u2103");
        Day5_MaxTemp.setText(Constants.List_MaxTemperature.get(4)+ "\u2103");

        Day6_Image.setImageBitmap(bitmapArray[5]);
        Day6_Weekday.setText(Constants.List_Day.get(5));
        Day6_MinTemp.setText(Constants.List_MinTemperature.get(5)+ "\u2103");
        Day6_MaxTemp.setText(Constants.List_MaxTemperature.get(5)+ "\u2103");
    }

}