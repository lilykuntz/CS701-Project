package com.example.freeseas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListItem extends AppCompatActivity {

    static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ListView listView;
    ListAdapter adapter;
    Drawable d;
    ProgressDialog loading;
    Bitmap myBitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_item);

        listView = findViewById(R.id.lv_items);

        // for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getItems();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        // Google App Script GET
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbymd1EdriVU5gYDRChzTRJvNmtyEdABoGROMiN1yt9szL6mx34/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    // parse json data
    public void parseItems(String jsonResponse) {
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String itemId = jo.getString("itemId");
                String date = jo.getString("date");
                String hullNumber = jo.getString("hullNumber");
                String description = jo.getString("description");
                String incident = jo.getString("incident");
                String longitude = jo.getString("longitude");
                String latitude = jo.getString("latitude");
                String country = jo.getString("country");
                String image = jo.getString("image");
                String video = jo.getString("video");

                HashMap<String, String> item = new HashMap<>();
                item.put("date", "Date: " + date);
                item.put("hullNumber", "Hull Number: " + hullNumber);
                item.put("description", "Description: " + description);
                item.put("incident", "Incident: " + incident);
                item.put("longitude", "Longitude: " + longitude);
                item.put("latitude","Latitude: " + latitude);
                item.put("country", "Country: " + country);
                item.put("image", image);
                item.put("video", video);
                // add each item in database to list
                list.add(item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"hullNumber","description","incident", "country"},new int[]{R.id.tv_hull_number,R.id.tv_description,R.id.tv_incident, R.id.tv_country});


        listView.setAdapter(adapter);

        // on click of list item display more info
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {

                Intent intent = new Intent(getApplicationContext(), VesselDetails.class);

                // send data over to Vessel Details as parameters
                Bundle b = new Bundle();
                b.putInt("id", (int) id); //Your id
                String hullNum = list.get((int) id).get("hullNumber");
                b.putString("hullNum", hullNum);
                String description = list.get((int) id).get("description");
                b.putString("description", description);
                String incident = list.get((int) id).get("incident");
                b.putString("incident", incident);
                String longitude = list.get((int) id).get("longitude");
                b.putString("longitude", longitude);
                String latitude = list.get((int) id).get("latitude");
                b.putString("latitude", latitude);
                String country = list.get((int) id).get("country");
                b.putString("country", country);
                String image = list.get((int) id).get("image");
                b.putString("image", image);
                String video = list.get((int) id).get("video");
                b.putString("video", video);
                String date = list.get((int) id).get("date");
                b.putString("date", date);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                //finish();

            }
        });
        Log.d("ADAP", String.valueOf(list.get(0).get("latitude")));
        loading.dismiss();
    }


}

