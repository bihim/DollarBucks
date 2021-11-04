package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class SurveyFinishActivity extends AppCompatActivity {
    private MaterialButton materialButtonAd;
    private MaterialButton materialButtonSkip;
    private String intentValue;
    private boolean isStartIo;
    private int retryAttempt;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_finish);
        intentValue = getIntent().getStringExtra("survey");
        isStartIo = getIntent().getBooleanExtra("isStartIo", true);
        findViewById();
        buttonCallBack();
        db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double balance = Double.parseDouble(task.getResult().getString("balance"));
                    balance = balance + 0.02;
                    Map<String, Object> data = new HashMap<>();
                    data.put("balance", String.format("%.2f", balance));
                    db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Logger.d("Balance Added");
                        }
                    });
                }
            }
        });
        setTimer();
    }

    private void setTimer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(intentValue)) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, getTime(intentValue));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
            editor.apply();
        } else {
            String getTime = sharedPreferences.getString(intentValue, "null");
            if (!getTime.equals("null")) {
                long getTimeInMillis = Long.parseLong(getTime);
                Calendar getCal = Calendar.getInstance();
                getCal.setTimeInMillis(getTimeInMillis);
                Calendar now = Calendar.getInstance();
                if (now.before(getCal)) {
                    long remaining = getCal.getTimeInMillis() - now.getTimeInMillis();
                    Calendar calendarRemaining = Calendar.getInstance();
                    calendarRemaining.setTimeInMillis(remaining);
                    printDifference(now.getTime(), getCal.getTime(), this);
                } else {
                    Logger.d("After");
                    now.add(Calendar.HOUR, getTime(intentValue));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
                    editor.apply();
                }
            }
        }
    }

    private int getTime(String value) {
        switch (value) {
            case "tech":
            case "movies":
                return 2;
            case "laptop":
                return 3;
            default:
                return 1;

        }
    }

    private void printDifference(Date startDate, Date endDate, Context context) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //Toasty.error(context, "Please wait for " + elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds + " to unlock this video", Toasty.LENGTH_SHORT).show();
    }

    private void buttonCallBack() {
        materialButtonAd.setOnClickListener(v -> {
            if (isStartIo) {
                StartAppAd.showAd(this);
            } else {
                AppLovinSdk.getInstance(this).setMediationProvider("max");
                AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
                    @Override
                    public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                        MaxRewardedAd maxRewardedAd = MaxRewardedAd.getInstance(GlobalVals.applovinReward, SurveyFinishActivity.this);
                        //Logger.d("Is ad ready: " + interstitialAd.isReady());
                        maxRewardedAd.loadAd();
                        maxRewardedAd.showAd();
                        maxRewardedAd.setListener(new MaxRewardedAdListener() {
                            @Override
                            public void onRewardedVideoStarted(MaxAd ad) {

                            }

                            @Override
                            public void onRewardedVideoCompleted(MaxAd ad) {
                            }

                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {

                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Logger.d("Is ad ready: " + maxRewardedAd.isReady());
                                retryAttempt = 0;
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {

                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                maxRewardedAd.loadAd();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                // Interstitial ad failed to load
                                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                                retryAttempt++;
                                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        maxRewardedAd.loadAd();
                                    }
                                }, delayMillis);
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                // Interstitial ad failed to display. We recommend loading the next ad
                                maxRewardedAd.loadAd();
                            }
                        });
                    }
                });
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        materialButtonSkip.setOnClickListener(v -> {
            if (isStartIo) {
                final StartAppAd rewardedVideo = new StartAppAd(this);
                rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
                rewardedVideo.showAd();
                rewardedVideo.setVideoListener(new VideoListener() {
                    @Override
                    public void onVideoCompleted() {
                        Logger.d("I am at finished");
                    }
                });

            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void findViewById() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        materialButtonSkip = findViewById(R.id.survey_finish_skip);
        materialButtonAd = findViewById(R.id.survey_finish_ad);
    }
}