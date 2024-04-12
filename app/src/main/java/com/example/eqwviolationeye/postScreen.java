package com.example.eqwviolationeye;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class postScreen extends AppCompatActivity {
    TextView dayDateTime,location;
    LinearLayout dayDateTimeLayout,locationLayout;
    CardView dayDateTimeCard,locationCard,uploadCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        dayDateTime = findViewById(R.id.dayDateTime);
        dayDateTimeLayout=findViewById(R.id.dayDateTiemLayout);
        dayDateTimeCard = findViewById(R.id.dayDateTiemCard);
        locationCard = findViewById(R.id.locationCard);
        uploadCard = findViewById(R.id.uploadCard);


        dayDateTimeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = (dayDateTime.getVisibility() == View.GONE)? View.VISIBLE:View.GONE;
                TransitionManager.beginDelayedTransition(dayDateTimeLayout,new AutoTransition());
                dayDateTime.setVisibility(visibility);
                dayDateTime.setText(getIntent().getStringExtra("date"));
            }
        });

        //Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();

    }

}