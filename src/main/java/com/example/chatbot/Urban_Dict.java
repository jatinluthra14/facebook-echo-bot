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

public class Urban_Dict {

    // http://localhost:8080/RESTfulExample/json/product/get
    public String getMeaning(String word) {

        try {
            word = word.startsWith(" ") ? word.substring(1) : word;
            URL url = new URL("http://api.urbandictionary.com/v0/define?term=".concat(word));
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
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
               jsonstr = jsonstr.concat(output);
            }

            JsonParser parser = new JsonParser();
            JsonObject json = null;
            json = (JsonObject) parser.parse(jsonstr);

            JsonArray jsonarr = json.getAsJsonArray("list");
            String def = jsonarr.get(0).getAsJsonObject().get("definition").toString();
            System.out.println(def);
            conn.disconnect();
            return def;

        } catch (MalformedURLException e) {

            e.printStackTrace();
            return "";

        } catch (IOException e) {

            e.printStackTrace();
            return "";

        }

    }

}
