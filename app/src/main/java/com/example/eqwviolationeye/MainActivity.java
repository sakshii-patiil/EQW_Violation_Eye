package com.example.eqwviolationeye;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //private FirebaseAuth firebaseAuth;
    static String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DBHelper dbHelper = new DBHelper(MainActivity.this);
//        data = dbHelper.readData();
//        Log.e("data" , data);
        //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();

//        if(data!="Not found")
//        {
//            startActivity(new Intent(MainActivity.this, IncidentReportScreen.class));
//            finish();
//        }else{
//            startActivity(new Intent(MainActivity.this, LoginScreen.class));
//            finish();
//        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, navigate to main screen or perform necessary actions
            // Example: startActivity(new Intent(MainActivity.this, HomeActivity.class));
            Intent i = new Intent(MainActivity.this, IncidentReportScreen.class);
            startActivity(i);
            finish();
        } else {
            // User is not signed in, navigate to sign-in screen or perform necessary actions
            // Example: startActivity(new Intent(MainActivity.this, SignInActivity.class));
            startActivity(new Intent(MainActivity.this, LoginScreen.class));
            finish();
        }
    }
}




