package com.example.freeseas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.FileProvider;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private ImageView image;
    private VideoView video;
    private MediaController m;

    private String pictureFilePath, pictureFile;
    private String videoFilePath, videoFile;

    private FirebaseStorage firebaseStorage;
    EditText editTextHullNumber, editTextDescription, editTextCountry, editTextIncident;
    TextView editTextLongitude, editTextLatitude;
    Button buttonAddItem, btnTakePic, btnTakeVid;
    private StorageReference mStorageRef;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // for back buttons
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get elements from add_tem.xml
        setContentView(R.layout.add_item);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        editTextHullNumber = findViewById(R.id.et_hull_number);
        editTextDescription = findViewById(R.id.et_description);
        editTextIncident = findViewById(R.id.et_incident);
        editTextLongitude = findViewById(R.id.et_longitude);
        editTextLatitude = findViewById(R.id.et_latitude);
        editTextCountry = findViewById(R.id.et_country);
        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);
        btnTakePic = findViewById(R.id.btnTakePic);
        btnTakePic.setOnClickListener(capture);
        image = findViewById(R.id.image);
        btnTakeVid = findViewById(R.id.btnTakeVid);
        btnTakeVid.setOnClickListener(captureVid);
        video = findViewById(R.id.video);

        // if there is no camera enabled
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            btnTakePic.setEnabled(false);
            btnTakeVid.setEnabled(false);
        }

        firebaseStorage = FirebaseStorage.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object SWITCH!!
                            editTextLatitude.setText(String.valueOf(location.getLongitude()));
                            editTextLongitude.setText(String.valueOf(location.getLatitude()));
                        }
                    }
                });
    }

    private View.OnClickListener capture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                sendTakePictureIntent();
            }
        }
    };

    private View.OnClickListener captureVid = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                dispatchTakeVideoIntent();
            }
        }
    };

    // take video
    private void dispatchTakeVideoIntent() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
           // startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            File videoFile = null;
            try {
                videoFile = getVideoFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Video file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(this,
                        "com.example.freeseas.fileprovider",
                        videoFile);
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    // save video as new file
    private File getVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        videoFile = "FREESEAS_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File movie = File.createTempFile(videoFile,  ".MOV", storageDir);
        videoFilePath = movie.getAbsolutePath();
        return movie;
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

    // take picture
    private void sendTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.freeseas.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    // save image as new file
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        pictureFile = "FREESEAS_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    // on image/video capture, save image/video and set the image/video component to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            if(pictureFilePath != null) {
                File imgFile = new File(pictureFilePath);
                if (imgFile.exists()) {
                    image.setImageURI(Uri.fromFile(imgFile));
                }
            }
            if(videoFilePath != null) {
                File vidFile = new File(videoFilePath);
                if (vidFile.exists()) {
                    video.setVideoURI(Uri.fromFile(vidFile));
                    video.setVisibility(View.VISIBLE);
                    video.start();
                }
            }
        }
    }



    //save captured picture on cloud storage
    private void addToCloudStorage() {
        File f = new File(pictureFilePath);
        File fv = new File(videoFilePath);
        Uri picUri = Uri.fromFile(f);
        Uri vidUri = Uri.fromFile(fv);
        final String cloudFilePath = picUri.getLastPathSegment();
        final String cloudFilePathV = vidUri.getLastPathSegment();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference storageRefV = firebaseStorage.getReference();
        StorageReference uploadRef = storageRef.child(cloudFilePath);
        StorageReference uploadRefV = storageRefV.child(cloudFilePathV);

        uploadRef.putFile(picUri).addOnFailureListener(new OnFailureListener(){
            public void onFailure(@NonNull Exception exception){
                Log.e(TAG,"Failed to upload picture to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                Toast.makeText(AddItem.this,
                        "Image has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
            }
        });

        uploadRefV.putFile(vidUri).addOnFailureListener(new OnFailureListener(){
            public void onFailure(@NonNull Exception exception){
                Log.e(TAG,"Failed to upload video to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                Toast.makeText(AddItem.this,
                        "Video has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls
    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String hullNumber = editTextHullNumber.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String incident = editTextIncident.getText().toString().trim();
        final String longitude = editTextLongitude.getText().toString().trim();
        final String latitude = editTextLatitude.getText().toString().trim();
        final String country = editTextCountry.getText().toString().trim();
        final String image = pictureFilePath.trim();
        final String video = videoFilePath.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbymd1EdriVU5gYDRChzTRJvNmtyEdABoGROMiN1yt9szL6mx34/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //here we pass params
                params.put("action", "addItem");
                params.put("hullNumber", hullNumber);
                params.put("description", description);
                params.put("incident", incident);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("country", country);
                params.put("image", image);
                params.put("video", video);

                return params;

            }
        };


        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }




    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            System.out.println();
            addToCloudStorage();
            addItemToSheet();

        }

    }
}