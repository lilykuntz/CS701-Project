package com.example.freeseas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private ImageView image;
    private String pictureFilePath, pictureFile;
    private FirebaseStorage firebaseStorage;
    EditText editTextHullNumber,editTextDescription,editTextLongitude,editTextLatitude,editTextCountry;
    Button buttonAddItem, btnTakePic;
    private StorageReference mStorageRef;

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
        editTextLongitude = findViewById(R.id.et_longitude);
        editTextLatitude = findViewById(R.id.et_latitude);
        editTextCountry = findViewById(R.id.et_country);
        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);
        btnTakePic = findViewById(R.id.btnTakePic);
        btnTakePic.setOnClickListener(capture);
        image = findViewById(R.id.image);

        // if there is no camera enabled
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            btnTakePic.setEnabled(false);
        }

        firebaseStorage = FirebaseStorage.getInstance();

    }

    private View.OnClickListener capture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                sendTakePictureIntent();
            }
        }
    };

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

    // on image capture, save image and set the image component to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                image.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    //save captured picture on cloud storage
    private void addToCloudStorage() {
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = picUri.getLastPathSegment();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference uploadRef = storageRef.child(cloudFilePath);

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
    }

    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls
    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String hullNumber = editTextHullNumber.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String longitude = editTextLongitude.getText().toString().trim();
        final String latitude = editTextLatitude.getText().toString().trim();
        final String country = editTextCountry.getText().toString().trim();
        final String image = pictureFilePath.trim();

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
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("country", country);
                params.put("image", image);
                System.out.println("countrt!!!!:" + country);
                System.out.println("path!!!!:" + image);
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