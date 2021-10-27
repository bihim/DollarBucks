package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton goToRegistration, signUp;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById();
        buttonCallBacks();
    }

    private void buttonCallBacks() {
        goToRegistration.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });
        signUp.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toasty.error(this, "Please fill all of the fields", Toast.LENGTH_SHORT, true).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Logger.d("User successfully logged in");
                        if (task.getResult() != null) {
                            if (task.getResult().getUser() != null) {
                                /*Logger.d("Getting UID: " + task.getResult().getUser().getUid());
                                Map<String, Object> data = new HashMap<>();
                                String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                data.put("deviceID", android_id);
                                db.collection("profile").document(task.getResult().getUser().getUid()).collection("info") //here sports is dynamic
                                        .document("info").set(data, SetOptions.merge())
                                        .addOnSuccessListener(unused -> {
                                            Logger.d("Added Successfully");
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Logger.e("Failure: " + e.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(this, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
                                        });*/
                            }
                        }
                        Toasty.success(this, "Login Successful", Toasty.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(GlobalVals.login, true);
                        editor.apply();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Logger.e(task.getException() + "");
                        Toasty.error(this, "Something went wrong. Error:" + task.getException(), Toasty.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void findViewById() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        Logger.addLogAdapter(new AndroidLogAdapter());
        goToRegistration = findViewById(R.id.go_to_registration);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        signUp = findViewById(R.id.login_sign_up);
        progressBar = findViewById(R.id.progress_log);
    }
}