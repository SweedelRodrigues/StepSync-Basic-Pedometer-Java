package com.example.stepsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int ACTIVITY_RECOGNITION_REQUEST = 100;

    TextView txtSteps;
    Button btnReset, btnHistory;

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    int todaySteps = 0;
    int initialSteps = -1;

    SharedPreferences prefs;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dbRef;

    String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // If user is not logged in, redirect to login
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Firebase DB reference
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Today's date
        todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Local storage
        prefs = getSharedPreferences("StepData", MODE_PRIVATE);
        todaySteps = prefs.getInt(todayDate, 0);

        // UI
        txtSteps = findViewById(R.id.txtSteps);
        btnReset = findViewById(R.id.btnReset);
        btnHistory = findViewById(R.id.btnHistory);
        Button btnLogout = findViewById(R.id.btnLogout);

        txtSteps.setText(String.valueOf(todaySteps));

        // LOGOUT BUTTON SETUP
        btnLogout.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });



        // Permission request
        requestActivityRecognitionPermission();

        // Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepCounterSensor == null && stepDetectorSensor == null) {
            Toast.makeText(this, "Your device does not support step counting", Toast.LENGTH_LONG).show();
        }

        // Reset button
        btnReset.setOnClickListener(v -> {
            todaySteps = 0;
            initialSteps = -1;

            txtSteps.setText("0");

            prefs.edit().putInt(todayDate, 0).apply();
            saveStepsToFirebase(0);

            Toast.makeText(MainActivity.this, "Steps reset!", Toast.LENGTH_SHORT).show();
        });

        // History page
        btnHistory.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
        });
    }

    private void requestActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    ACTIVITY_RECOGNITION_REQUEST
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // STEP COUNTER SENSOR (global count)
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            if (initialSteps == -1) {
                initialSteps = (int) event.values[0];
            }

            todaySteps = (int) event.values[0] - initialSteps;
        }

        // STEP DETECTOR (1 step per trigger)
        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            todaySteps++;
        }

        txtSteps.setText(String.valueOf(todaySteps));

        // Save locally
        prefs.edit().putInt(todayDate, todaySteps).apply();

        // Save to Firebase
        saveStepsToFirebase(todaySteps);
    }

    private void saveStepsToFirebase(int steps) {

        if (user == null) return;

        DatabaseReference ref = dbRef.child(user.getUid())
                .child("steps")
                .child(todayDate);

        ref.setValue(steps)
                .addOnSuccessListener(aVoid ->
                        {} // silent success
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firebase save failed!", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();

        if (stepCounterSensor != null)
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

        else if (stepDetectorSensor != null)
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}

