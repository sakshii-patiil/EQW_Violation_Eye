package com.example.eqwviolationeye;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class postScreen extends AppCompatActivity {
    TextView dayDateTime,location;
    LinearLayout dayDateTimeLayout,locationLayout;
    private DatabaseReference mDatabase;
    String date;


    CardView dayDateTimeCard,locationCard,uploadCard;

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
        locationLayout = findViewById(R.id.locationLayout);
        Toast.makeText(postScreen.this, LoginScreen.id, Toast.LENGTH_SHORT).show();
        date = getIntent().getStringExtra("date");

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
                mDatabase = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/").getReference(LoginScreen.id).child("pending").child(date);


                // Path to the child node you want to retrieve
                DatabaseReference childRef = mDatabase.child("Location");
                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String childData = snapshot.getValue(String.class);
                        //Toast.makeText(getApplicationContext(),childData,Toast.LENGTH_SHORT).show();
                        if (childData != null) {
                            // Do something with the retrieved data
                            location.setText(childData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

}