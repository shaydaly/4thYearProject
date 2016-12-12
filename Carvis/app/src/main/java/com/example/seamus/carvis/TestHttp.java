package com.example.seamus.carvis;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TestHttp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_http);


    }

    public void makeHttpRequest(View view) {
        final TextView textView = (TextView) findViewById(R.id.Result);

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="http://www.openstreetmap.org/api/0.6/way/48290550";
        //String url ="http://overpass-api.de/api/interpreter?data=[out:json];way(4402297);out;";
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=53.3509124&longitude=-6.37792";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        ID(response);
                        String str = response;
                        textView.setText(str+" is the max speed");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
            }


        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "CARVIS");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public String ID(String json) {
        //return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
        String maxSpeed = "_";
        try {
            ObjectMapper mapper = new ObjectMapper();
            // JsonNode jsonNode = mapper.readValue(json,JsonNode.class);
            JSONObject jsonO = new JSONObject(json);
            //maxSpeed= String.valueOf(jsonNode.get("elements.tags"));
            maxSpeed = jsonO.get("osm_id").toString();
            System.out.println(maxSpeed);


            final TextView textView = (TextView) findViewById(R.id.Result);

            RequestQueue queue = Volley.newRequestQueue(this);
            //String url ="http://www.openstreetmap.org/api/0.6/way/48290550";
            String url = "http://overpass-api.de/api/interpreter?data=[out:json];way(" + maxSpeed + ");out;";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            textView.setText(getNewSpeed(response));

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText(error.toString());
                }


            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("User-agent", "CARVIS");
                    return headers;
                }
            };
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (Exception e) {

        }

        return getNewSpeed("Seamus");

    }


    public String getNewSpeed(String json) {
        String s = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            // JsonNode jsonNode = mapper.readValue(json,JsonNode.class);
            JSONObject jsonO = new JSONObject(json);
            //maxSpeed= String.valueOf(jsonNode.get("elements.tags"));
            s = jsonO.get("elements").toString();

            JSONArray ja = new JSONArray(s);
            //for(int i=0; i<ja.length(); i++){
                JSONObject j = (JSONObject)ja.get(0);
                JSONObject jo = (JSONObject)j.get("tags");
            

            s= String.valueOf(jo.get("maxspeed"));

           // }



        } catch (JSONException e) {

        }
        return s;
    }

}
