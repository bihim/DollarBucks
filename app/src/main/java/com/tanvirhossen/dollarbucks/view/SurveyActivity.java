package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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
import com.tanvirhossen.dollarbucks.adapter.SurveyAdapter;
import com.tanvirhossen.dollarbucks.global.GlobalVals;
import com.tanvirhossen.dollarbucks.model.SurveyModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class SurveyActivity extends AppCompatActivity {

    private ArrayList<SurveyModel> surveyModelArrayList;
    private SurveyAdapter surveyAdapter;
    private TextView textViewToolbar;
    private TextView textViewQuestions;
    private RecyclerView recyclerView;
    private MaterialToolbar materialToolbar;
    private ArrayList<String> questionsArraylist;
    private ImageButton imageButtonBack;
    private ProgressBar progressBar;
    private int initialIndex = 0;
    private int count = 0;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private boolean isStartIo;
    private int retryAttempt;
    private String intentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        findViewById();
        setSupportActionBar(materialToolbar);
        //setupSurveyModel();
        intentValue = getIntent().getStringExtra("survey");
        isStartIo = getIntent().getBooleanExtra("isStartIo", true);
        getAllQuestions(intentValue);
        imageButtonBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void getAllQuestions(String docPath) {
        db.collection("survey").document(docPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    surveyModelArrayList = new ArrayList<>();
                    Map<String, Object> map = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Logger.d(entry.getKey());
                        ArrayList<String> answers = (ArrayList<String>) entry.getValue();
                        StringBuilder answerBuilder = new StringBuilder();
                        for (String answer : answers) {
                            answerBuilder.append(answer);
                            answerBuilder.append(",");
                        }
                        surveyModelArrayList.add(new SurveyModel(1, entry.getKey(), answerBuilder.toString()));
                        setupRecyclerview(surveyModelArrayList);
                        Logger.d(answers);
                    }

                }

            }
        });
    }

    private void setupRecyclerview(ArrayList<SurveyModel> surveyModelArrayList) {
        String topQuestion = surveyModelArrayList.get(initialIndex).getQuestion();
        String questionList = surveyModelArrayList.get(initialIndex).getOption();
        String[] questions = questionList.split(",");
        questionsArraylist = new ArrayList<>();
        questionsArraylist.addAll(Arrays.asList(questions));
        textViewQuestions.setText(topQuestion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        surveyAdapter = new SurveyAdapter(questionsArraylist, this);
        recyclerView.setAdapter(surveyAdapter);
        textViewToolbar.setText((initialIndex + 1) + " Out of " + surveyModelArrayList.size());
        surveyAdapter.setOnItemClickListener(position -> {
            String topQuestionUpdated = surveyModelArrayList.get(initialIndex).getQuestion();
            String questionListUpdated = surveyModelArrayList.get(initialIndex).getOption();
            String[] questionsUpdated = questionListUpdated.split(",");
            Logger.d("Questionssssss " + questionsUpdated[position]);
            Date c = Calendar.getInstance().getTime();
            progressBar.setVisibility(View.VISIBLE);
            //System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            Map<String, Object> data = new HashMap<>();
            data.put(topQuestionUpdated, questionsUpdated[position]);

            db.collection("profile").document(firebaseAuth.getUid()).collection("survey") //here sports is dynamic
                    .document("sports").collection("date").
                    document(formattedDate).set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Logger.d("Added Successfully");
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Logger.e("Failure: " + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(this, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
                    });

            if (surveyModelArrayList.size() > initialIndex + 1) {
                //Logger.d(initialIndex);
                showDialog(this);
                questionsArraylist.clear();
                textViewToolbar.setText((initialIndex + 1) + " Out of " + surveyModelArrayList.size());
                String topQuestions = surveyModelArrayList.get(initialIndex).getQuestion();
                textViewQuestions.setText(topQuestions);
                String questionLists = surveyModelArrayList.get(initialIndex).getOption();
                String[] questionsInc = questionLists.split(",");
                //Logger.d("Arrays of question: "+Arrays.toString(questionsInc));
                questionsArraylist.addAll(Arrays.asList(questionsInc));
                //Logger.d("Selected Question: "+questionsArraylist.get(position));
                surveyAdapter.notifyDataSetChanged();
            } else {
                showDialogFinish(this);
                Logger.e("I am at else");
            }
        });
    }

    private void setupSurveyModel() {
        surveyModelArrayList = new ArrayList<>();
        surveyModelArrayList.add(new SurveyModel(1, "What is your favorite os?", "Linux,Windows,MacOS"));
        surveyModelArrayList.add(new SurveyModel(2, "What is your favorite framework?", "React,Flutter,Android"));
        surveyModelArrayList.add(new SurveyModel(3, "What is your favorite hearing system?", "Loud,Speaker,Headphone"));
        surveyModelArrayList.add(new SurveyModel(4, "What is your favorite country?", "Bangladesh,India,Pakistan"));
        surveyModelArrayList.add(new SurveyModel(5, "What is your favorite food?", "Noodles,Kacchi,Biriani"));
        surveyModelArrayList.add(new SurveyModel(6, "What is your favorite pet?", "Cat,Dog,Parrot"));
        surveyModelArrayList.add(new SurveyModel(7, "What is your favorite healthy food?", "Rice,Spinach,Egg"));
        surveyModelArrayList.add(new SurveyModel(8, "What is your favorite person?", "Father,Mother,Wife"));
    }

    private void findViewById() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        materialToolbar = findViewById(R.id.survey_toolbar);
        textViewToolbar = findViewById(R.id.survey_toolbar_text);
        textViewQuestions = findViewById(R.id.survey_questions);
        recyclerView = findViewById(R.id.survey_recyclerview);
        progressBar = findViewById(R.id.survey_progress);
        imageButtonBack = findViewById(R.id.imageButton);
    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_survey_ad);

        MaterialButton materialButtonAd = dialog.findViewById(R.id.dialog_ad);
        MaterialButton materialButtonSkip = dialog.findViewById(R.id.dialog_skip);
        TextView textViewCountdown = dialog.findViewById(R.id.dialog_countdown);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(15 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        textViewCountdown.setVisibility(View.VISIBLE);
                        textViewCountdown.setText("Wait for " + millisUntilFinished / 1000 + " secons to appear button");
                        materialButtonAd.setVisibility(View.GONE);
                        materialButtonSkip.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        textViewCountdown.setVisibility(View.GONE);
                        materialButtonAd.setVisibility(View.VISIBLE);
                        materialButtonSkip.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        }, 10);

        db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double balance = Double.parseDouble(task.getResult().getString("balance"));
                    balance = balance + 0.1;
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

        initialIndex++;
        materialButtonAd.setOnClickListener(v -> {
            db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        int bonusAds = Integer.parseInt(task.getResult().getString("bonusAds"));
                        bonusAds = bonusAds + 1;
                        Map<String, Object> data = new HashMap<>();
                        data.put("bonusAds", String.valueOf(bonusAds));
                        db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Logger.d("Bonus Added");
                            }
                        });
                    }
                }
            });

            if (isStartIo) {
                StartAppAd.showAd(this);
                dialog.dismiss();
            } else {
                AppLovinSdk.getInstance(this).setMediationProvider("max");
                AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
                    @Override
                    public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                        MaxRewardedAd maxRewardedAd = MaxRewardedAd.getInstance(GlobalVals.applovinReward, SurveyActivity.this);
                        //Logger.d("Is ad ready: " + interstitialAd.isReady());
                        maxRewardedAd.loadAd();
                        maxRewardedAd.showAd();
                        maxRewardedAd.setListener(new MaxRewardedAdListener() {
                            @Override
                            public void onRewardedVideoStarted(MaxAd ad) {

                            }

                            @Override
                            public void onRewardedVideoCompleted(MaxAd ad) {
                                dialog.dismiss();
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
                dialog.dismiss();
            }

            //Toasty.info(activity, "Ad showing", Toasty.LENGTH_SHORT).show();
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
                dialog.dismiss();
            } else {
                dialog.dismiss();
            }

        });

        dialog.show();

    }

    public void showDialogFinish(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_survey_ad);

        MaterialButton materialButtonAd = dialog.findViewById(R.id.dialog_ad);
        MaterialButton materialButtonSkip = dialog.findViewById(R.id.dialog_skip);
        TextView textViewCountdown = dialog.findViewById(R.id.dialog_countdown);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(15 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        textViewCountdown.setVisibility(View.VISIBLE);
                        textViewCountdown.setText("Wait for " + millisUntilFinished / 1000 + " secons to appear button");
                        materialButtonAd.setVisibility(View.GONE);
                        materialButtonSkip.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        textViewCountdown.setVisibility(View.GONE);
                        materialButtonAd.setVisibility(View.VISIBLE);
                        materialButtonSkip.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        }, 10);

        db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double balance = Double.parseDouble(task.getResult().getString("balance"));
                    balance = balance + 0.1;
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

        materialButtonAd.setOnClickListener(v -> {
            if (isStartIo) {
                StartAppAd.showAd(this);
                intentCall();
                /*startActivity(new Intent(activity, SurveyFinishActivity.class));
                finish();*/
                dialog.dismiss();
            } else {
                AppLovinSdk.getInstance(this).setMediationProvider("max");
                AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
                    @Override
                    public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                        MaxRewardedAd maxRewardedAd = MaxRewardedAd.getInstance(GlobalVals.applovinReward, SurveyActivity.this);
                        //Logger.d("Is ad ready: " + interstitialAd.isReady());
                        maxRewardedAd.loadAd();
                        maxRewardedAd.showAd();
                        maxRewardedAd.setListener(new MaxRewardedAdListener() {
                            @Override
                            public void onRewardedVideoStarted(MaxAd ad) {

                            }

                            @Override
                            public void onRewardedVideoCompleted(MaxAd ad) {
                                dialog.dismiss();
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
                /*startActivity(new Intent(activity, SurveyFinishActivity.class));
                finish();*/
                intentCall();
                dialog.dismiss();
            }

            //Toasty.info(activity, "Ad showing", Toasty.LENGTH_SHORT).show();
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
                /*startActivity(new Intent(activity, SurveyFinishActivity.class));
                finish();*/
                intentCall();
                dialog.dismiss();
            } else {
                /*startActivity(new Intent(activity, SurveyFinishActivity.class));
                finish();*/
                intentCall();
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    private void intentCall() {
        Intent intent = new Intent(this, SurveyFinishActivity.class);
        intent.putExtra("survey", intentValue);
        intent.putExtra("isStartIo", isStartIo);
        startActivity(intent);
        finish();
    }

    private void interstitialAd() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
            }
        }, 15000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}