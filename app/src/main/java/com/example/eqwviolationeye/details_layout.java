package com.example.eqwviolationeye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.BuildConfig;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class details_layout extends AppCompatActivity {
    String currentUser;
    DatabaseReference databaseReference, databaseRef;
    private FirebaseAuth firebaseAuth;

    TextView title, tags;
    VideoView videoView;
    EditText details, mentions;
    String date,id;
    Button post;
    private DatabaseReference mDatabase;

    Spinner numberplates;
    String liscenceplate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_layout);

        title = findViewById(R.id.title);
        details = findViewById(R.id.description);
        videoView = findViewById(R.id.videoView);
        post = findViewById(R.id.postBtn);
        numberplates = findViewById(R.id.tags);

        date = getIntent().getStringExtra("date");


        title.setText(date);
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                // Set the type of the content to be shared
                shareIntent.setType("*/*");

                // Add the text data to the Intent

                String shareTitle = String.valueOf(date);
                String shareSubject = String.valueOf(details);

                shareIntent.putExtra(Intent.EXTRA_TITLE, shareTitle);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                shareIntent.putExtra(Intent.EXTRA_TEXT,liscenceplate);
                // Add the video URI to the Intent
                ArrayList<Uri> uris = new ArrayList<>();
                Uri videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/eqw-violationeye-42382.appspot.com/o/demo1%20(1)%20(1)%20(1)%20(1).mp4?alt=media&token=afbcb720-793a-4d14-aa06-ce3109109d5d"); // Replace with actual video URI
                uris.add(videoUri);

                // Add URIs to the Intent
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                // Start the sharing chooser dialog
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });



//        firebaseAuth = FirebaseAuth.getInstance();
//        String userId = String.valueOf(firebaseAuth.getCurrentUser().getUid());
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Reports").child(userId).child(report);
//        FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);

//        Toast.makeText(getApplicationContext(), id + date,Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos/video.mp4");


        databaseRef =FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date).child("image_url");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String videoUrl = dataSnapshot.getValue(String.class);
                    if (videoUrl != null) {
                        // Use Glide to download the video from the URL
                        Glide.with(getApplicationContext())
                                .load(videoUrl)
                                .downloadOnly(new SimpleTarget<File>() {

                                    @Override
                                    public void onResourceReady(@NonNull File resource, @Nullable com.bumptech.glide.request.transition.Transition<? super File> transition) {

                                        Toast.makeText(getApplicationContext(), "Video Downloaded", Toast.LENGTH_SHORT).show();
                                        // Create a Uri for the downloaded video file
                                        Uri videoUri = Uri.fromFile(resource);

                                        // Add the video URI as an extra to the Intent
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);

                                        String filePath = resource.getAbsolutePath();

                                        // Set the path of the downloaded video file to the VideoView
                                        videoView.setVideoPath(filePath);

                                        // Start playback
                                        videoView.start();

                                    }
                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        // Handle download failure
                                    }

                                });
                    } else {
                        // Handle case where video URL is null
                    }
                } else {
                    // Handle case where video data does not exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve description and latitude values
//                    String description = dataSnapshot.child("description").getValue(String.class);
                    String location = dataSnapshot.child("Location").getValue(String.class);
                    String videoDownloadUrl= dataSnapshot.child("image_url").getValue(String.class);
                    // Now you have the description and latitude values
                    String np = dataSnapshot.child("number_plates").getValue(String.class);
                    // Now you have the description and latitude values
                    Log.d("Video Download URL ", videoDownloadUrl);


                    ArrayList<String> arr = new ArrayList<>(Arrays.asList(np.split(",")));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arr);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Set the ArrayAdapter to the Spinner
                    numberplates.setAdapter(arrayAdapter);



                    numberplates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            liscenceplate = parent.getItemAtPosition(position).toString();
                            Toast.makeText(getApplicationContext(),liscenceplate,Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                        }
                    });

                    String message = "The above vehicle is seen violating the traffic rules. I request @"+ location.substring(0,location.indexOf(",")) + "CityPolice to take necessary actions asap. It is causing unnecessary chaos in "+ location.substring(location.indexOf(",")+1) +" area";
                    details.setText(message);



//                    location.setText(location);
                } else {
                    Log.d("Data", "No data exists at this location");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching data", databaseError.toException());
            }
        });


    }
    // Method to play the video in VideoView
    private void playVideo(String filePath) {
        // Set the local file path of the downloaded video to the VideoView
        videoView.setVideoPath(filePath);
        // Start playing the video
        videoView.start();
    }
}