package com.learnandroid.weathertoday;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    TextView centigrade, fahrenheit, latitude, longitude;
    TextInputEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        centigrade = findViewById(R.id.centigrade);
        fahrenheit = findViewById(R.id.fahrenheit);
        latitude = findViewById(R.id.lat);
        longitude = findViewById(R.id.longitude);
        editText = findViewById(R.id.cityName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void showResults(View view) {
        String cityName = Objects.requireNonNull(editText.getText()).toString();
        getWeatherData(cityName);
    }

    public void getWeatherData(String city) {
        String url = "https://api.weatherapi.com/v1/current.json?key=35c9f92ac5bf4df0811144140212307&q=" + city + "&aqi=no";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject location = response.getJSONObject("location");
                            JSONObject current = response.getJSONObject("current");
                            String lat = location.getString("lat");
                            String lon = location.getString("lon");
                            String temp_c = current.getString("temp_c");
                            String temp_f = current.getString("temp_f");
                            updateData(lat, lon, temp_c, temp_f);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    public void updateData(String lat, String lon, String temp_c, String temp_f) {
        centigrade.setText(getString(R.string.temperature_in_centigrade) + temp_c);
        fahrenheit.setText(getString(R.string.temperature_in_fahrenheit) + temp_f);
        latitude.setText( getString(R.string.latitude) + lat);
        longitude.setText( getString(R.string.longitude)+ lon);

    }
}