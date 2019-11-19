package com.example.freeseas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class VesselDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        String hullNum = null, description = null, longitude = null, latitude = null, image = null, country = null, date = null;
        if(b != null) {
            hullNum = b.getString("hullNum");
            description = b.getString("description");
            longitude = b.getString("longitude");
            latitude = b.getString("latitude");
            country = b.getString("country");
            image = b.getString("image");
            date = b.getString("date");
        }

        setContentView(R.layout.vessel_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReferenceFromUrl("gs://free-seas-255114.appspot.com/");
        System.out.println("IMAGE!!!!!: "+ image);
        if(image != null) {
            File imgFile = new File(image);
            if(imgFile.exists()) {
                String pathName = image;
                Bitmap myBitmap = BitmapFactory.decodeFile(pathName);
                ImageView img = findViewById(R.id.imageView2);
                img.setImageBitmap(myBitmap);
            }
        }

        TextView hullNumber = findViewById(R.id.hullNumber);
        TextView descrip = findViewById(R.id.description);
        TextView lon = findViewById(R.id.longitude);
        TextView lat = findViewById(R.id.latitude);
        TextView cntry = findViewById(R.id.country);
        TextView dt = findViewById(R.id.date);



        hullNumber.setText(hullNum);
        descrip.setText(description);
        lon.setText(longitude);
        lat.setText(latitude);
        cntry.setText(country);
        dt.setText(date);

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
}
