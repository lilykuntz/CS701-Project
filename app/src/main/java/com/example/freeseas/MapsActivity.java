package com.example.freeseas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    Button buttonAddItem, buttonListItem;
    static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);
        buttonListItem = findViewById(R.id.btn_list_items);
        buttonListItem.setOnClickListener(this);
    }

    public void onClick(View v) {

        // on click of "Report"
        if (v == buttonAddItem) {

            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            startActivity(intent);
        }

        // on click of "List All"
        if (v == buttonListItem) {

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            startActivity(intent);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);

        // on click of marker info window, pass parameters to VesselDetails.java
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), VesselDetails.class);
                Bundle b = new Bundle();
                int index = Integer.parseInt(marker.getSnippet().substring(4));
                b.putString("hullNum", "Hull Number: " + list.get(index).get("hullNumber"));
                String description = list.get(index).get("description");
                b.putString("description", "Description: " + description);
                String longitude = list.get(index).get("longitude");
                b.putString("longitude", "Longitude: " + longitude);
                String latitude = list.get(index).get("latitude");
                b.putString("latitude", "Latitude: " + latitude);
                String country = list.get(index).get("country");
                b.putString("country", "Country: " + country);
                String image = list.get(index).get("image");
                b.putString("image", image);
                String date = list.get(index).get("date");
                b.putString("date", "Date: " + date);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                //finish();

            }
        });

        // Google App Script GET
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbymd1EdriVU5gYDRChzTRJvNmtyEdABoGROMiN1yt9szL6mx34/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            JSONArray jarray = jobj.getJSONArray("items");


                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jo = jarray.getJSONObject(i);

                                // parse json data
                                String itemId = jo.getString("itemId");
                                String date = jo.getString("date");
                                String hullNumber = jo.getString("hullNumber");
                                String description = jo.getString("description");
                                String longitude = jo.getString("longitude");
                                String latitude = jo.getString("latitude");
                                String country = jo.getString("country");
                                String image = jo.getString("image");

                                HashMap<String, String> item = new HashMap<>();
                                item.put("itemId", itemId);
                                item.put("date", date);
                                item.put("hullNumber", hullNumber);
                                item.put("description", description);
                                item.put("longitude",longitude);
                                item.put("latitude",latitude);
                                item.put("country", country);
                                item.put("image", image);

                                // add vessel item to list
                                list.add(item);

                                Marker[] marker = new Marker[0];

                                // for each item in our database, add a marker with corresponding data
                                for(int m = 0; m < list.size(); m++){
                                    Marker newMarker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.valueOf(list.get(m).get("longitude")), Double.valueOf(list.get(m).get("latitude"))))
                                            .title("Hull number: " + list.get(m).get("hullNumber"))
                                            .snippet("ID: " + m)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        Log.d("ADAP", String.valueOf(list.size()));

        // Add a marker in Philippines, and move the camera.
        final LatLng philippines = new LatLng(13, 118);
        mMap.addMarker(new MarkerOptions()
                .position(philippines)
                .title("You are here.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines, 7));
    }


    // make the marker clickable
    @Override
    public boolean onMarkerClick(Marker marker) {{

    }
        return false;
    }
}