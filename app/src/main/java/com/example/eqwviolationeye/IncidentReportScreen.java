package com.example.eqwviolationeye;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eqwviolationeye.databinding.ActivityIncidentReportScreenBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class IncidentReportScreen extends AppCompatActivity {


    static String timestamp;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    FloatingActionButton mic;
    private TextView tv_Speech_to_text;
    static boolean flag = false;
    ActivityIncidentReportScreenBinding binding;
    String loc;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing other items
        // from layout file

        // method to get the location

        binding = ActivityIncidentReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mic = findViewById(R.id.button);

        replaceFragment(new pendingFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.history) {
                    replaceFragment(new historyFragment());
                } else {
                    replaceFragment(new pendingFragment());
                }
                return true;
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Say violation", Toast.LENGTH_SHORT).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                if (Objects.requireNonNull(result).get(0).equals("violation")) {
                    Date currentTime = Calendar.getInstance().getTime();
                    //Toast.makeText(getApplicationContext(),Objects.requireNonNull(result).get(0),Toast.LENGTH_SHORT).show();
                    timestamp = currentTime.toString();
                    timestamp = timestamp.substring(0, timestamp.indexOf("G"));

                    fetchLocation();
                    Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void fetchLocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationUpdates();
    }
    private void requestLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission granted, get location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            //Toast.makeText(IncidentReportScreen.this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();

                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    StringBuilder addressStringBuilder = new StringBuilder();

                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        addressStringBuilder.append(address.getAddressLine(i)).append("\n");
                                    }

                                    String locality = address.getSubLocality();
                                    String postalCode = address.getPostalCode();
                                    String city = address.getLocality();
                                    String state = address.getAdminArea();
                                    loc = locality + ", "+ city + ", " + state + ", " + postalCode;
                                    Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
                                    addDataToFirebase();
//                                    addressTextView.setText(completeAddress);
                                } else {
//                                    addressTextView.setText("Address not found");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(IncidentReportScreen.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void addDataToFirebase() {

        // Initialize firebase user
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eqw-violationeye-42382-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference(LoginScreen.id);
        myRef.child("pending").child(timestamp).setValue(false);
        myRef.child("pending").child(timestamp).child("Location").setValue(loc);
    }
}