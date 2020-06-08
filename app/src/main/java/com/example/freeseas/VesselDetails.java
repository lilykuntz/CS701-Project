package com.example.freeseas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class VesselDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get parameters that were passed over
        Bundle b = getIntent().getExtras();
        String hullNum = null, description = null, incident = null, longitude = null, latitude = null, image = null, video = null, country = null, date = null;
        if(b != null) {
            hullNum = b.getString("hullNum");
            description = b.getString("description");
            incident = b.getString("incident");
            longitude = b.getString("longitude");
            latitude = b.getString("latitude");
            country = b.getString("country");
            image = b.getString("image");
            video = b.getString("video");
            date = b.getString("date");
        }
        setContentView(R.layout.vessel_details);

        // for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReferenceFromUrl("gs://free-seas-255114.appspot.com/");

        if(image != null) {
            File imgFile = new File(image);
            if(imgFile.exists()) {
                String pathName = image;
                Bitmap myBitmap = BitmapFactory.decodeFile(pathName);
                ImageView img = findViewById(R.id.imageView2);
                img.setImageBitmap(myBitmap);
            }
        }
        if(video != null) {
            File vidFile = new File(video);
            if(vidFile.exists()) {
                String vpathName = video;
                final VideoView vid = findViewById(R.id.videoView);
                vid.setVideoURI(Uri.fromFile(vidFile));
                vid.setVisibility(View.VISIBLE);
                vid.start();
                vid.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        vid.start();
                    }
                });
            }
        }

        // get the components of the page
        TextView hullNumber = findViewById(R.id.hullNumber);
        TextView descrip = findViewById(R.id.description);
        TextView inc = findViewById(R.id.incident);
        TextView lon = findViewById(R.id.longitude);
        TextView lat = findViewById(R.id.latitude);
        TextView cntry = findViewById(R.id.country);
        TextView dt = findViewById(R.id.date);


        // set the components to the corresponding fields
        hullNumber.setText(hullNum);
        descrip.setText(description);
        inc.setText(incident);
        lon.setText(longitude);
        lat.setText(latitude);
        cntry.setText(country);
        dt.setText(date);

    }

//    public void playVideo(View v) {
//        m = new MediaController(this);
//        video.setMediaController(m);
//        File vidFile = new File(videoFilePath);
//        if (vidFile.exists()) {
//            video.setVideoURI(Uri.fromFile(vidFile));
//            video.setVisibility(View.VISIBLE);
//            video.start();
//        }
//    }

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
