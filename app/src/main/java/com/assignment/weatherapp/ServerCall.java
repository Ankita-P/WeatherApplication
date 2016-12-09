package com.assignment.weatherapp;

/**
 * Created by ankitapatil on 12/8/16.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ankitapatil on 11/24/16.
 * File contains code to send and receive data from API
 */

public class ServerCall {
    private static URL API_URL = null;

    public String GetWeatherData(String RequestData)
    {
        HttpURLConnection connection = null;
        InputStream iStream =  null;

        try
        {
            API_URL = new URL(RequestData);
            connection = (HttpURLConnection)API_URL.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            //Read response obtained
            StringBuffer buffer = new StringBuffer();
            iStream = connection.getInputStream();
            BufferedReader Reader = new BufferedReader(new InputStreamReader(iStream));
            String line = null;
            while((line = Reader.readLine()) != null) {
                buffer.append(line);
            }
            iStream.close();
            connection.disconnect();
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
