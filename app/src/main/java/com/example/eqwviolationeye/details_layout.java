package com.example.eqwviolationeye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class details_layout extends AppCompatActivity {
    String currentUser;
    DatabaseReference databaseReference;
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

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(videoDownloadUrl);
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Handle successful download URL retrieval
                            String downloadUrl = uri.toString();
                            downloadVideo(downloadUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors retrieving the download URL
                            Log.e("TAG", "Error getting download URL", e);
                        }
                    });
                } else {
                    Log.d("Data", "No data exists at this location");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching data", databaseError.toException());
            }
        });



//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.report_voilation:
//                        startActivity(new Intent(ReportTemplate.this, MainActivity.class));
//                        return true;
//                    case R.id.voilation_list:
//                        startActivity(new Intent(ReportTemplate.this, ResultsActivity.class));
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });

    }



    private void downloadVideo(String videoDownloadUrl) {
        // Create a reference to the video file in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoDownloadUrl);

        // Get a temporary file path to store the downloaded video
        File localFile = null;
        try {
            localFile = File.createTempFile("video", "mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (localFile != null) {
            // Download the video file to local storage
            File finalLocalFile = localFile;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Play the downloaded video in VideoView
                    playVideo(finalLocalFile.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors downloading the video file
                    Log.e("TAG", "Error downloading video file", e);
                }
            });
        }
    }

    // Method to play the video in VideoView
    private void playVideo(String filePath) {
        // Set the local file path of the downloaded video to the VideoView
        videoView.setVideoPath(filePath);
        // Start playing the video
        videoView.start();
    }
}