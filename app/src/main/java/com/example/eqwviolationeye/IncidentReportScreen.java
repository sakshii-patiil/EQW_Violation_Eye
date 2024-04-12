package com.example.eqwviolationeye;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eqwviolationeye.databinding.ActivityIncidentReportScreenBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class IncidentReportScreen extends AppCompatActivity {


    static String timestamp;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    FloatingActionButton mic;
    private TextView tv_Speech_to_text;
    static boolean flag = false;
    ActivityIncidentReportScreenBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIncidentReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mic = findViewById(R.id.button);

        replaceFragment(new pendingFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.history)
                {
                    replaceFragment(new historyFragment());
                }
                else
                {
                    replaceFragment(new pendingFragment());
                }
                return true;
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Say violation",Toast.LENGTH_SHORT).show();
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                    FirebaseAuth.getInstance().signOut();
                } catch (Exception e) {

                }
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                if(Objects.requireNonNull(result).get(0).equals("violation"))
                {
                    Date currentTime = Calendar.getInstance().getTime();
                    //Toast.makeText(getApplicationContext(),Objects.requireNonNull(result).get(0),Toast.LENGTH_SHORT).show();
                    timestamp = currentTime.toString();
                    timestamp = timestamp.substring(0,timestamp.indexOf("G"));
                    addDataToFirebase();
                    Toast.makeText(getApplicationContext(),timestamp,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void addDataToFirebase() {

        // Initialize firebase user
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference(LoginScreen.id);
        myRef.child("pending").child(timestamp).setValue(false);
    }

}