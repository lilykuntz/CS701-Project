package com.example.freeseas;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        listView = (ListView) findViewById(R.id.lv_items);

        getItems();

    }


    public void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

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
                String longitude = jo.getString("longitude");
                String latitude = jo.getString("latitude");
                String country = jo.getString("country");


                HashMap<String, String> item = new HashMap<>();
                item.put("itemId", itemId);
                item.put("date", date);
                item.put("hullNumber", hullNumber);
                item.put("description", description);
                item.put("longitude",longitude);
                item.put("latitude",latitude);
                item.put("country", country);

                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"itemId", "date", "hullNumber","description","longitude","latitude","country"},new int[]{R.id.tv_date, R.id.tv_hull_number,R.id.tv_description,R.id.tv_longitude,R.id.tv_latitude,R.id.tv_country});

        listView.setAdapter(adapter);
        Log.d("ADAP", String.valueOf(list.get(0).get("latitude")));
        loading.dismiss();
    }


}
