package com.example.eqwviolationeye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
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
    TextView dayDateTime,location;
    LinearLayout dayDateTimeLayout,locationLayout,uploadLayout;
    private DatabaseReference mDatabase;
    String date;
    String locationData;
    String id="";

    CardView dayDateTimeCard,locationCard,submitCard,uploadCard;
    String[] subAddresses;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        dayDateTime = findViewById(R.id.dayDateTime);
        location = findViewById(R.id.location);
        dayDateTimeLayout=findViewById(R.id.dayDateTiemLayout);
        dayDateTimeCard = findViewById(R.id.dayDateTiemCard);
        locationCard = findViewById(R.id.locationCard);
        uploadCard = findViewById(R.id.uploadCard);
        submitCard = findViewById(R.id.submitCard);
        locationLayout = findViewById(R.id.locationLayout);

        date = getIntent().getStringExtra("date");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);


        mDatabase = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date);

        fetchFromDatabse();

        dayDateTimeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = (dayDateTime.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(dayDateTimeLayout,new AutoTransition());
                dayDateTime.setVisibility(visibility);
                dayDateTime.setText(date);
            }
        });

        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = (location.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(locationLayout,new AutoTransition());

                location.setVisibility(visibility);
                location.setText(locationData);

            }
        });

        submitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
                pickImageIntent.setType("image/*");

                // Start the activity to pick an image
                startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST_CODE);

                mDatabase.child("status").setValue("false");
            }
        });


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
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationData = snapshot.getValue(String.class);

                if (locationData != null) {
                    // Do something with the retrieved data
                    subAddresses = locationData.split(", ");
                    Toast.makeText(getApplicationContext(),subAddresses[1],Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Location Not Found",Toast.LENGTH_SHORT).show();
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