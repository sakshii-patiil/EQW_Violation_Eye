package com.example.eqwviolationeye;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;


public class postScreen extends AppCompatActivity {
//    TextView dayDateTime,location;
//    LinearLayout dayDateTimeLayout,locationLayout,uploadLayout;
//    private DatabaseReference mDatabase;
//    String date;
//    String locationData;
    String id="";

    TextView dayDateTime,location;
    LinearLayout dayDateTimeLayout,locationLayout,uploadLayout,details;
    FrameLayout processedFrame,detailsFrame,postFrame;
    private DatabaseReference mDatabase;
    String date,detailsDate;
    String locationData;


    LinearLayout submitCard,upload;
    String[] subAddresses;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private String statusData;

    //    CardView dayDateTimeCard,locationCard,submitCard,uploadCard;
//    String[] subAddresses;
//    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);


        dayDateTime = findViewById(R.id.dayDateTime);
        location = findViewById(R.id.location);
        upload = findViewById(R.id.upload);
        postFrame=findViewById(R.id.postFrame);
        processedFrame=findViewById(R.id.processedFrame);
        detailsFrame=findViewById(R.id.detailsFrame);
        details = findViewById(R.id.details);
//        dayDateTimeLayout=findViewById(R.id.dayDateTiemLayout);
//        dayDateTimeCard = findViewById(R.id.dayDateTiemCard);
//        locationCard = findViewById(R.id.locationCard);
//        uploadCard = findViewById(R.id.uploadCard);
//        submitCard = findViewById(R.id.submit);
//        locationLayout = findViewById(R.id.locationLayout);

        date = getIntent().getStringExtra("date");
        detailsDate = date;

        Toast.makeText(getApplicationContext(), date,Toast.LENGTH_SHORT).show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);

//        Toast.makeText(getApplicationContext(), id + date,Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date);

        fetchFromDatabse();


        dayDateTime.setText(date);



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://127.0.0.1:5000";
                try {
                    Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Chrome is probably not installed
                }
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), details_layout.class);
                i.putExtra("date", detailsDate);
                Toast.makeText(getApplicationContext(),detailsDate,Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });

//        dayDateTime = findViewById(R.id.dayDateTime);
//        location = findViewById(R.id.location);
//        dayDateTimeLayout=findViewById(R.id.dayDateTiemLayout);
//        dayDateTimeCard = findViewById(R.id.dayDateTiemCard);
//        locationCard = findViewById(R.id.locationCard);
//        uploadCard = findViewById(R.id.uploadCard);
//        submitCard = findViewById(R.id.submitCard);
//        locationLayout = findViewById(R.id.locationLayout);

//        date = getIntent().getStringExtra("date");

//        dayDateTimeCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int visibility = (dayDateTime.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
//                TransitionManager.beginDelayedTransition(dayDateTimeLayout,new AutoTransition());
//                dayDateTime.setVisibility(visibility);
//                dayDateTime.setText(date);
//            }
//        });
//
//        locationCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int visibility = (location.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
//                TransitionManager.beginDelayedTransition(locationLayout,new AutoTransition());
//
//                location.setVisibility(visibility);
//                location.setText(locationData);
//
//            }
//        });
//
//        submitCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
//                pickImageIntent.setType("image/*");
//
//                // Start the activity to pick an image
//                startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST_CODE);
//
//                mDatabase.child("status").setValue("false");
//            }
//        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the URI of the selected image
                Uri imageUri = data.getData();

                // Get the text from the tweet EditText

                String message = "The above vehicle {number plate} is seen violating the traffic rules. I request @"+subAddresses[1]+"CityPolice to take necessary actions asap. It is causing unnecessary chaos in "+subAddresses[0]+" area";

                uploadImageToFirebaseStorage(imageUri);
                // Create a new Intent
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/*");

                // Start the activity to share the data
                startActivity(Intent.createChooser(shareIntent, "Share via"));


            }
        }
    }

    void fetchFromDatabse()
    {
        date = date.trim();
        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();


        DatabaseReference childRef = mDatabase.child("Location");
        DatabaseReference childRefStatus = mDatabase.child("status");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationData = snapshot.getValue(String.class);

                if (locationData != null) {
                    // Do something with the retrieved data
                    subAddresses = locationData.split(", ");
                    location.setText(subAddresses[0]+", "+subAddresses[1]);
//                    Toast.makeText(getApplicationContext(),subAddresses[1],Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Location Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        childRefStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusData = snapshot.getValue(String.class);

                if(statusData.equals("false"))
                {
                    postFrame.setVisibility(View.VISIBLE);
                    detailsFrame.setVisibility(View.VISIBLE);
                    processedFrame.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateFirebaseDatabase(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }


    private void updateFirebaseDatabase(String imageUrl) {
        mDatabase.child("image_url").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {

                });
    }

}