package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.startapp.sdk.adsbase.StartAppAd;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.api.JsonPlaceHolder;
import com.tanvirhossen.dollarbucks.global.GlobalVals;
import com.tanvirhossen.dollarbucks.model.CountryModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private LinearLayout materialButtonCahsout, survey, youtube, proof, profile, rateNowLayout;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private MaterialButton rateNow;
    private int retryAttempt;
    private TextView pdf1Title, pdf2Title, pdf3Title, pdf4Title, pdf5Title;
    private TextView pdf1sub, pdf2sub, pdf3sub, pdf4sub, pdf5sub;
    private MaterialButton buttonPdf1, buttonPdf2, buttonPdf3, buttonPdf4, buttonPdf5;
    private TextView online, paid, todayUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        buttonCallBacks();
        setTimer("PDF1", pdf1Title, pdf1sub, GlobalVals.pdf1title, GlobalVals.pdf1Story, buttonPdf1);
        setTimer("PDF2", pdf2Title, pdf2sub, GlobalVals.pdf2Title, GlobalVals.pdf2Story, buttonPdf2);
        setTimer("PDF3", pdf3Title, pdf3sub, GlobalVals.pdf3Title, GlobalVals.pdf3Story, buttonPdf3);
        setTimer("PDF4", pdf4Title, pdf4sub, GlobalVals.pdf4Title, GlobalVals.pdf4Story, buttonPdf4);
        setTimer("PDF5", pdf5Title, pdf5sub, GlobalVals.pdf5Title, GlobalVals.pdf5Story, buttonPdf5);
        setData();
        checkCountry();
    }

    private void buttonCallBacks() {
        boolean isRated = sharedPreferences.getBoolean(GlobalVals.isRated, false);
        rateNowLayout.setVisibility(isRated ? View.GONE : View.VISIBLE);

        materialButtonCahsout.setOnClickListener(v -> {
            startActivity(new Intent(this, WithdrawActivity.class));

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
        rateNow.setOnClickListener(v -> {
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
                                final StartAppAd rewardedVideo = new StartAppAd(MainActivity.this);
                                rewardedVideo.loadAd();
                                rewardedVideo.showAd();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(GlobalVals.isRated, true);
                                editor.apply();
                                rateNowLayout.setVisibility(View.GONE);
                                launchMarket();
                            }
                        });
                    }
                }
            });
        });
    }

    private void findViewById() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        materialButtonCahsout = findViewById(R.id.cashout);
        survey = findViewById(R.id.goto_survey);
        youtube = findViewById(R.id.main_tv);
        proof = findViewById(R.id.main_proof);
        profile = findViewById(R.id.my_profile);
        rateNowLayout = findViewById(R.id.rate_now_layout);
        rateNow = findViewById(R.id.rate_now);

        pdf1Title = findViewById(R.id.titlePDF1);
        pdf2Title = findViewById(R.id.titlePDF2);
        pdf3Title = findViewById(R.id.titlePDF3);
        pdf4Title = findViewById(R.id.titlePDF4);
        pdf5Title = findViewById(R.id.titlePDF5);

        pdf1sub = findViewById(R.id.storyPDF1);
        pdf2sub = findViewById(R.id.storyPDF2);
        pdf3sub = findViewById(R.id.storyPDF3);
        pdf4sub = findViewById(R.id.storyPDF4);
        pdf5sub = findViewById(R.id.storyPDF5);

        buttonPdf1 = findViewById(R.id.btnPDF1);
        buttonPdf2 = findViewById(R.id.btnPDF2);
        buttonPdf3 = findViewById(R.id.btnPDF3);
        buttonPdf4 = findViewById(R.id.btnPDF4);
        buttonPdf5 = findViewById(R.id.btnPDF5);

        online = findViewById(R.id.online);
        paid = findViewById(R.id.paid);
        todayUser = findViewById(R.id.today_user);
    }

    private void checkCountry() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iplist.cc/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<CountryModel> call = jsonPlaceHolderApi.getCountry();
        ArrayList<String> arrayListCountry = new ArrayList<>();
        arrayListCountry.add("AF");
        arrayListCountry.add("AM");
        arrayListCountry.add("AZ");
        arrayListCountry.add("BH");
        arrayListCountry.add("BD");
        arrayListCountry.add("BT");
        arrayListCountry.add("KH");
        arrayListCountry.add("BN");
        arrayListCountry.add("CN");
        arrayListCountry.add("CX");
        arrayListCountry.add("CC");
        arrayListCountry.add("IO");
        arrayListCountry.add("GE");
        arrayListCountry.add("BT");
        arrayListCountry.add("HK");
        arrayListCountry.add("IN");
        arrayListCountry.add("ID");
        arrayListCountry.add("IR");
        arrayListCountry.add("IQ");
        arrayListCountry.add("IL");
        arrayListCountry.add("JP");
        arrayListCountry.add("JO");
        arrayListCountry.add("KZ");
        arrayListCountry.add("KW");
        arrayListCountry.add("KG");
        arrayListCountry.add("LA");
        arrayListCountry.add("LB");
        arrayListCountry.add("MO");
        arrayListCountry.add("MY");
        arrayListCountry.add("MV");
        arrayListCountry.add("MN");
        arrayListCountry.add("MM");
        arrayListCountry.add("NP");
        arrayListCountry.add("KP");
        arrayListCountry.add("OM");
        arrayListCountry.add("PK");
        arrayListCountry.add("PS");
        arrayListCountry.add("PH");
        arrayListCountry.add("QA");
        arrayListCountry.add("SA");
        arrayListCountry.add("SG");
        arrayListCountry.add("LK");
        arrayListCountry.add("BT");

        call.enqueue(new Callback<CountryModel>() {
            @Override
            public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
                if (arrayListCountry.contains(response.body().getCountrycode())) { //!check here
                    db.collection("notice").document("notice").get().addOnSuccessListener(documentSnapshot -> {
                        boolean notice = documentSnapshot.getBoolean("notice");
                        if (notice) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("You are an outsider")
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                    //Toasty.error(RegistrationActivity.this, "You are not eligible", Toasty.LENGTH_SHORT).show();
                } else {
                    //Toasty.success(RegistrationActivity.this, "You are eligible", Toasty.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<CountryModel> call, Throwable t) {

            }
        });
    }

    private void setData() {
        db.collection("timer").document("timer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String onlines = task.getResult().getString("online");
                String today_users = task.getResult().getString("today_user");
                String paids = task.getResult().getString("paid");
                online.setText("Online: " + onlines);
                todayUser.setText("Today Users: " + today_users);
                paid.setText("Paid: " + paids);
            }
        });
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void setTimer(String intentValue, TextView title, TextView subTitle, String titleText, String subTitleText, MaterialButton materialButton) {
        title.setText(titleText);
        subTitle.setText(subTitleText);
        materialButton.setOnClickListener(v -> {
            db.collection("timer").document("timer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    int timeInSec = Integer.parseInt(task.getResult().getString("article"));
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    if (!sharedPreferences.contains(intentValue)) {
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.SECOND, timeInSec);

                        Calendar nowNow = Calendar.getInstance();
                        Date afterAdd = now.getTime();
                        Date nowAdd = nowNow.getTime();

                        Timer timer = new Timer();
                        TimerTask youtubeTask = new SurveyText(intentValue);
                        timer.schedule(youtubeTask, afterAdd.getTime() - nowAdd.getTime());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, PDFReaderActivity.class);
                        intent.putExtra("top", titleText);
                        intent.putExtra("story", subTitleText);
                        startActivity(intent);
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

                                Timer timer = new Timer();
                                TimerTask youtubeTask = new SurveyText(intentValue);
                                timer.schedule(youtubeTask, getCal.getTime().getTime() - now.getTime().getTime());

                                printDifference(now.getTime(), getCal.getTime(), MainActivity.this);
                            } else {
                                Logger.d("After");
                                now.add(Calendar.SECOND, timeInSec);
                                Calendar nowNow = Calendar.getInstance();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
                                editor.apply();
                                Intent intent = new Intent(MainActivity.this, PDFReaderActivity.class);
                                intent.putExtra("top", titleText);
                                intent.putExtra("story", subTitleText);
                                Timer timer = new Timer();
                                TimerTask youtubeTask = new SurveyText(intentValue);
                                timer.schedule(youtubeTask, now.getTime().getTime() - nowNow.getTime().getTime());
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        });
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

        Toasty.error(context, "Please wait for " + elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds + " to unlock this article", Toasty.LENGTH_SHORT).show();
    }

    class SurveyText extends TimerTask {
        final String text;

        public SurveyText(String text) {
            this.text = text;
        }

        public void run() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "PDF")
                    .setSmallIcon(R.drawable.ic_info_outline_white_24dp)
                    .setContentTitle("PDF")
                    .setContentText("Your " + text + " is available to complete.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Survey";
                String description = "Your " + text + " is available to complete.";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("PDF", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(12334789, builder.build());
            Logger.d("I am in survey timerTask");
        }
    }
}