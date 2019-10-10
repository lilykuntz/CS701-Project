package com.example.freeseas;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

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
        final LatLng philippines = new LatLng(13, 118);
        mMap.addMarker(new MarkerOptions()
                .position(philippines)
                .title("Marker in the Philippines")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines, 5));
        final Button button = findViewById(R.id.button_id);
        final Button buttonPlace = findViewById(R.id.button_id2);
        final Button buttonDone = findViewById(R.id.button_id3);
        final FrameLayout frameLayout = findViewById(R.id.frame_id);
        final RadioButton dangerous = findViewById(R.id.radioButton4);
        final EditText name = findViewById(R.id.description);
        final List<BitmapDescriptor> colors;
        final Marker[] newMarker = new Marker[1];

        colors = Arrays.asList(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frameLayout.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            }
        });
        buttonPlace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                newMarker[0] = mMap.addMarker(new MarkerOptions()
                        .position(philippines)
                        .title(name.getText().toString())
                        .draggable(true)
                        .icon(colors.get(dangerous.isChecked() ? 1 : 0)));
                frameLayout.setVisibility(View.GONE);
                buttonDone.setVisibility(View.VISIBLE);

            }
        });
        buttonDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newMarker[0].setDraggable(false);
                button.setVisibility(View.VISIBLE);
                buttonDone.setVisibility(View.GONE);
            }
        });
    }
}