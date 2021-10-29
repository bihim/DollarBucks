package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private LinearLayout materialButtonLogout, survey, youtube, proof, profile;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        buttonCallBacks();
    }

    private void buttonCallBacks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        materialButtonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
            editor.putBoolean(GlobalVals.login, false);
            editor.apply();
        });
        survey.setOnClickListener(v -> {
            startActivity(new Intent(this, SurveyListActivity.class));
        });
        youtube.setOnClickListener(v -> {
            startActivity(new Intent(this, YoutubeActivity.class));
        });
        proof.setOnClickListener(v -> {
            startActivity(new Intent(this, ProofActivity.class));
        });
        profile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void findViewById() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        materialButtonLogout = findViewById(R.id.logout);
        survey = findViewById(R.id.goto_survey);
        youtube = findViewById(R.id.main_tv);
        proof = findViewById(R.id.main_proof);
        profile = findViewById(R.id.my_profile);
    }
}