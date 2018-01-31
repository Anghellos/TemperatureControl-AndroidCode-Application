package com.example.aggelos.tempcontrol.MQTTServer;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aggel on 12/18/2017.
 */

public class OpenWeatherMap {

    public double ParseJson(String... urls) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = reader.read();
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject weatherDatas = new JSONObject(jsonObject.getString("main"));

                double temperature = Double.parseDouble(weatherDatas.getString("temp"));
                return  temperature;
                //int tempIn = (int) (temperature*1.8-459.67);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}