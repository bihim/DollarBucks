package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.api.JsonPlaceHolder;
import com.tanvirhossen.dollarbucks.global.GlobalVals;
import com.tanvirhossen.dollarbucks.model.CountryModel;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onStart() {
        super.onStart();
        //checking if current user exists or not
        //if not then setting the sharedPref value accordingly
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GlobalVals.login, currentUser != null);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginAuth();
    }

    private void loginAuth() {
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        //these are self explanatory
        if (!sharedPreferences.contains(GlobalVals.login)) {
            Logger.d("No Login Field");
            gotoActivity(RegistrationActivity.class);
        } else {
            boolean isLoggedIn = sharedPreferences.getBoolean(GlobalVals.login, false);
            if (isLoggedIn) {
                Logger.d("User Logged In");
                gotoActivity(MainActivity.class);
            } else {
                Logger.d("Not Logged In");
                gotoActivity(RegistrationActivity.class);
            }
        }
    }

    private void gotoActivity(Class<?> className) {
        //these are self explanatory
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(SplashScreenActivity.this, className));
            finish();
        }, 2000);
    }
}