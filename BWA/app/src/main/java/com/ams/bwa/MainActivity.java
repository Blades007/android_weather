package com.ams.bwa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView CityName;
    Button search;
    TextView show;
    String url;


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class getWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {


                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputstream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader((inputstream)));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");

                }
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String weatherInfo = jsonObject.getString("main");

                     /*   "temp":296.15,
                        "feels_like":296.71,
                        "temp_min":296.15,
                        "temp_max":296.15,
                        "pressure":1012,
                        "humidity":69*/
                    weatherInfo = weatherInfo.replace("temp", "Temperature");
                    weatherInfo = weatherInfo.replace("feels_like", "Feels Like");
                    weatherInfo = weatherInfo.replace("min", "Min");
                    weatherInfo = weatherInfo.replace("max", "Max");
                    weatherInfo = weatherInfo.replace("pressure", "Air Pressure");
                    weatherInfo = weatherInfo.replace("humidity", "Humidity");
                    weatherInfo = weatherInfo.replace("sea", "Sea");
                    weatherInfo = weatherInfo.replace("level", "Level");
                    weatherInfo = weatherInfo.replace("grnd", "Ground");

                    weatherInfo = weatherInfo.replace("_", " ");
                    weatherInfo = weatherInfo.replace(",", "\n");
                    weatherInfo = weatherInfo.replace("feels_like", "feels Like");
                    weatherInfo = weatherInfo.replace(",", "\n");
                    weatherInfo = weatherInfo.replace("{", "");
                    weatherInfo = weatherInfo.replace("{", "");
                    weatherInfo = weatherInfo.replace(":", "     ");
                    weatherInfo = weatherInfo.replace("}", "");
                    weatherInfo = weatherInfo.replace("\"", "");
                /*weatherInfo=weatherInfo.replace(",","\n");
                weatherInfo=weatherInfo.replace("temp","temparature");*/


                    show.setText(weatherInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                show.setText("Please make sure whether you entered correct Spelling of the Location/Don't Leave search box empty");
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        if (isOnline()) {
            CityName = findViewById((R.id.CityName));
            search = findViewById((R.id.search));
            show = findViewById((R.id.show));
            final String[] temp = {""};

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline()) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        /*Toast.makeText(MainActivity.this,"Button Clicked", Toast.LENGTH_LONG).show();*/

                        String city = CityName.getText().toString();

                        try {
                            if (city != null) {
                                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=89d902d4a47805afef70507ac1036975";
                            }
                            if (CityName.getText() == null || CityName.getText().equals("")) {
                                Toast.makeText(MainActivity.this, "Enter city", Toast.LENGTH_LONG).show();
                            }
                            getWeather task = new getWeather();
                            temp[0] = task.execute(url).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        try {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Internet not available, Cross check your internet connectivity and try again later...")
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();

                                        }
                                    }).show();
                        } catch (Exception e) {
                            // Log.d(SyncStateContract.Constants.TAG, "Show Dialog: " + e.getMessage());
                        }
                    }
                }

            });
        }
        else {
            try {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity and try again later...")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();

                            }
                        }).show();
            } catch (Exception e) {
                // Log.d(SyncStateContract.Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }

    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

}