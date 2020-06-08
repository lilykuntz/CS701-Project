package com.example.freeseas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    Button buttonAddItem, buttonListItem;
    private FusedLocationProviderClient fusedLocationClient;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

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
                String incident = list.get(index).get("incident");
                b.putString("incident", "Incident: " + incident);
                String longitude = list.get(index).get("longitude");
                b.putString("longitude", "Longitude: " + longitude);
                String latitude = list.get(index).get("latitude");
                b.putString("latitude", "Latitude: " + latitude);
                String country = list.get(index).get("country");
                b.putString("country", "Country: " + country);
                String image = list.get(index).get("image");
                b.putString("image", image);
                String video = list.get(index).get("video");
                b.putString("video", video);
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
                                String incident = jo.getString("incident");
                                String longitude = jo.getString("longitude");
                                String latitude = jo.getString("latitude");
                                String country = jo.getString("country");
                                String image = jo.getString("image");
                                String video = jo.getString("video");

                                HashMap<String, String> item = new HashMap<>();
                                item.put("itemId", itemId);
                                item.put("date", date);
                                item.put("hullNumber", hullNumber);
                                item.put("description", description);
                                item.put("incident", incident);
                                item.put("longitude", longitude);
                                item.put("latitude", latitude);
                                item.put("country", country);
                                item.put("image", image);
                                item.put("video", video);

                                // add vessel item to list
                                list.add(item);

                                Marker[] marker = new Marker[0];

                                // for each item in our database, add a marker with corresponding data
                                for (int m = 0; m < list.size(); m++) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7));
                        }
                    }
                });
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}