package com.assignment.weatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ankitapatil on 12/9/16.
 */
public class SearchCity extends Activity {

    //Declaration of UI elements and Local Variables
    private ProgressDialog progress = null;
    private EditText CityName;
    private ListView CityNamesList;
    private String String_CityName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchcity);

        InstantiateUI();

        CityNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Storing City ID for the selected City
                Constants.CityID = Constants.List_CityID.get(position);

                //stating Activity to display Weather Information
                Intent i = new Intent(SearchCity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Button click event for Search button in layout file.
    public void SearchCity(View v)
    {
        String_CityName = CityName.getText().toString().trim();

        //Data validation for city name entered.
        if(String_CityName.length() > 0)
            new GetCityID().execute();
        else
            Toast.makeText(SearchCity.this, "Please provide city name!", Toast.LENGTH_LONG).show();
    }

    //Asynchronous task to get details of City entered by user.
    private class GetCityID extends AsyncTask<Void, Void, String[]> {

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
                Constants.ClearCityData();
                String RequestData = Constants.API_URL+"find?q="+String_CityName+"&cnt=30&appid="+Constants.API_KEY;

                //sending data to ServerCall class for calling API with requested parameters
                String response = Constants.serverCall.GetWeatherData(RequestData);
                JSONObject jobj = new JSONObject(response);
                JSONArray List_Array = jobj.getJSONArray("list");
                for(int i=0;i<List_Array.length();i++)
                {
                    JSONObject ItemObject = List_Array.getJSONObject(i);
                    long CityID = Long.parseLong(ItemObject.getString("id"));
                    String City_Name = ItemObject.getString("name");
                    JSONObject SysObject = ItemObject.getJSONObject("sys");
                    String City_Country = SysObject.getString("country");

                    City_Name +=","+City_Country;

                    //storing data in ArrayLists
                    Constants.List_CityID.add(CityID);
                    Constants.List_CityName.add(City_Name);
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
            //Data validation for information received from API
            if(Constants.List_CityID.isEmpty())
                Toast.makeText(SearchCity.this, "No City of the given name found.", Toast.LENGTH_LONG).show();
            else
                DisplayList();

            super.onPostExecute(result);
        }
    }

    //Function to instantiate all UI elements in the layout file
    public void InstantiateUI()
    {
        progress = new ProgressDialog(SearchCity.this);
        CityName = (EditText) findViewById(R.id.CityName);
        CityNamesList = (ListView) findViewById(R.id.CityNamesList);
    }

    //function to display next 6 days weather information in UI elements
    public void DisplayList()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.List_CityName);
        CityNamesList.setAdapter(arrayAdapter);
    }
}
