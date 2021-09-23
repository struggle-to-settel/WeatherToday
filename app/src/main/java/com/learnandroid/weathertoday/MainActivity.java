package com.learnandroid.weathertoday;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView gender;
    TextInputEditText pinCode, dob, address1, fullName;
    MaterialButton buttonCheck;
    RequestQueue requestQueue;
    String district, state;
    TextView District, State;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("register");

        //Finding Views
        gender = findViewById(R.id.gender);
        pinCode = findViewById(R.id.pincode);
        District = findViewById(R.id.textDistrict);
        State = findViewById(R.id.textState);
        dob = findViewById(R.id.dob);
        buttonCheck = findViewById(R.id.buttonCheck);
        address1 = findViewById(R.id.address1);
        fullName = findViewById(R.id.fullname);

        buttonCheck.setEnabled(false);
        myCalendar = Calendar.getInstance(TimeZone.getDefault());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        setUpButtonCheckPinCode(pinCode, buttonCheck);
        setUpGender(gender);


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String y = String.valueOf(year);
                String m = String.valueOf(month);
                String d = String.valueOf(dayOfMonth);
                updateLabel(y, m, d);
            }
        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,
                        date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

    }

    public void setUpGender(AutoCompleteTextView view) {
        String[] sex = {"Male", "Female"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sex);
        view.setThreshold(1);
        view.setAdapter(arrayAdapter);
    }

    public void setUpButtonCheckPinCode(TextInputEditText pinCode, MaterialButton buttonCheck) {
        pinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonCheck.setEnabled(s.length() > 5 && s.length() < 7);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void getDataFromPinCode(String pinCode) {

        String url = "https://api.postalpincode.in/pincode/" + pinCode;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            JSONArray postOffice = jsonObject.getJSONArray("PostOffice");
                            JSONObject first = postOffice.getJSONObject(0);
                            district = first.getString("District");
                            state = first.getString("State");
                            District.setText(getString(R.string.district)  +  district);
                            State.setText(getString(R.string.state) + state);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    public void onClickCheck(View view) {
        String pin = Objects.requireNonNull(pinCode.getText()).toString();
        getDataFromPinCode(pin);
    }

    private void updateLabel(String year, String month, String day) {
        String date = day + "/" + month + "/" + year;
        dob.setText(date);
    }

    public void move(View view) {

        if (allNotGood()) {
            Toast.makeText(this, "Field with * sign should not be null", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }
    }

    public boolean allNotGood() {
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(Objects.requireNonNull(pinCode.getText()).length() > 6 || pinCode.getText().length() < 6);
        booleans.add(Objects.requireNonNull(address1.getText()).length() < 3 || address1.getText().length() > 50);
        booleans.add(Objects.requireNonNull(fullName.getText()).length() < 1);
        booleans.add(Objects.requireNonNull(dob.getText()).length() < 1);
        final boolean add = booleans.add(gender.getText().length() < 1);
        return booleans.contains(true);
    }
}
