package com.example.freeseas;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Philippines, and move the camera.
        LatLng philippines = new LatLng(13, 118);
        mMap.addMarker(new MarkerOptions()
                .position(philippines)
                .title("Marker in the Philippines")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines, 5));
        final Button button = findViewById(R.id.button_id);
        final FrameLayout frameLayout = findViewById(R.id.frame_id);
        final List<BitmapDescriptor> colors;
        colors = Arrays.asList(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frameLayout.setVisibility(View.VISIBLE);
                LatLng rand = new LatLng(ThreadLocalRandom.current().nextInt(0, 20 + 1), ThreadLocalRandom.current().nextInt(110, 125 + 1));
                // Code here executes on main thread after user presses button
                mMap.addMarker(new MarkerOptions()
                        .position(rand)
                        .icon(colors.get(ThreadLocalRandom.current().nextInt(0, 1 + 1))));

            }
        });
    }
}