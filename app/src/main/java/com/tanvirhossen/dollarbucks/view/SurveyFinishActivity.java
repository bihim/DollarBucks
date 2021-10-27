package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.tanvirhossen.dollarbucks.R;

public class SurveyFinishActivity extends AppCompatActivity {
    private MaterialButton materialButtonAd;
    private MaterialButton materialButtonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_finish);
        findViewById();
        buttonCallBack();
    }

    private void buttonCallBack(){
        materialButtonAd.setOnClickListener(v->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        materialButtonSkip.setOnClickListener(v->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void findViewById() {
        materialButtonSkip = findViewById(R.id.survey_finish_skip);
        materialButtonAd = findViewById(R.id.survey_finish_ad);
    }
}