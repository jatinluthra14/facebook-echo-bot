package com.example.chatbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TimeZone {

    // http://localhost:8080/RESTfulExample/json/product/get
    public String getDateTime(String word) {

        try {
            word = word.startsWith(" ") ? word.substring(1) : word;
            URL url = new URL("http://api.timezonedb.com/v2/get-time-zone?key=TIL5CVHQ43ER&format=json&by=zone&zone=".concat(word));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            String jsonstr = new String("");
            while ((output = br.readLine()) != null) {
                jsonstr = jsonstr.concat(output);
            }

            JsonParser parser = new JsonParser();
            JsonObject json = null;
            json = (JsonObject) parser.parse(jsonstr);

            String datetime = new String(json.get("formatted").toString());
            System.out.println(datetime);
            conn.disconnect();
            return datetime;

        } catch (MalformedURLException e) {

            e.printStackTrace();
            return "";

        } catch (IOException e) {

            e.printStackTrace();
            return "";

        }

    }

}
