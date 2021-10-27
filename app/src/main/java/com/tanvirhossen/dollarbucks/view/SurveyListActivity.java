package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.card.MaterialCardView;
import com.tanvirhossen.dollarbucks.R;

public class SurveyListActivity extends AppCompatActivity {
    private MaterialCardView materialCardViewSports, materialCardViewTech, materialCardViewMobile, materialCardViewLaptop, materialCardViewMovies, materialCardViewCountry;
    private ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        findViewById();
        buttonCallBacks();

    }

    private void buttonCallBacks(){
        materialCardViewSports.setOnClickListener(v-> intentCall("sports"));
        materialCardViewTech.setOnClickListener(v-> intentCall("tech"));
        materialCardViewMobile.setOnClickListener(v-> intentCall("mobile"));
        materialCardViewLaptop.setOnClickListener(v-> intentCall("laptop"));
        materialCardViewMovies.setOnClickListener(v-> intentCall("movies"));
        materialCardViewCountry.setOnClickListener(v-> intentCall("country"));
        imageButtonBack.setOnClickListener(v-> onBackPressed());
    }

    private void findViewById(){
        materialCardViewSports = findViewById(R.id.survey_sports);
        materialCardViewTech = findViewById(R.id.survey_tech);
        materialCardViewMobile = findViewById(R.id.survey_mobile);
        materialCardViewLaptop = findViewById(R.id.survey_laptop);
        materialCardViewMovies = findViewById(R.id.survey_movies);
        materialCardViewCountry = findViewById(R.id.survey_country);
        imageButtonBack = findViewById(R.id.survey_list_back);
    }
    private void intentCall(String docValue){
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.putExtra("survey", docValue);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}