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


public class postScreen extends AppCompatActivity {
    TextView dayDateTime,location;
    LinearLayout dayDateTimeLayout,locationLayout,uploadLayout;
    private DatabaseReference mDatabase;
    String date;
    String locationData;


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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String id = currentUser.getEmail();
        id = id.substring(0,id.length()-10);
        date = date.trim();
        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(id).child("pending").child(date);


        // Path to the child node you want to retrieve
        DatabaseReference childRef = mDatabase.child("Location");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationData = snapshot.getValue(String.class);

                if (locationData != null) {
                    // Do something with the retrieved data
                    subAddresses = locationData.split(", ");
                    Toast.makeText(getApplicationContext(),subAddresses[1],Toast.LENGTH_SHORT).show();
                    enableUIElements();
//                            System.out.println(subAddresses[1]);
                }else{
                    Toast.makeText(getApplicationContext(),"Location Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void enableUIElements() {
        // Enable UI elements here
        locationCard.setEnabled(true);
        submitCard.setEnabled(true);
        // You can also update UI elements with the fetched data if needed
        // For example:
        location.setText(locationData);
    }

}