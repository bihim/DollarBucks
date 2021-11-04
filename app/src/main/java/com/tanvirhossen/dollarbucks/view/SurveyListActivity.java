package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import com.google.android.material.card.MaterialCardView;
import com.orhanobut.logger.Logger;
import com.startapp.sdk.adsbase.StartAppAd;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.adapter.YoutubeAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

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
        materialCardViewSports.setOnClickListener(v-> {
            setTimer("sports", true);
        });
        materialCardViewTech.setOnClickListener(v-> {
            setTimer("tech", false);
        });
        materialCardViewMobile.setOnClickListener(v-> {
            setTimer("mobile", true);
        });
        materialCardViewLaptop.setOnClickListener(v->{
            setTimer("laptop", false);
        });
        materialCardViewMovies.setOnClickListener(v-> {
            setTimer("movies", true);
        });
        materialCardViewCountry.setOnClickListener(v->{
            setTimer("country", false);
        });
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
    private void intentCall(String docValue, boolean isStartIo){
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.putExtra("survey", docValue);
        intent.putExtra("isStartIo", isStartIo);
        startActivity(intent);
    }

    private void setTimer(String intentValue, boolean isStartIo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(intentValue)) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, getTime(intentValue));

            Calendar nowNow = Calendar.getInstance();
            Date afterAdd = now.getTime();
            Date nowAdd = nowNow.getTime();

            Timer timer = new Timer();
            TimerTask youtubeTask = new SurveyText(intentValue);
            timer.schedule(youtubeTask, afterAdd.getTime() - nowAdd.getTime());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
            editor.apply();
            intentCall(intentValue, isStartIo);
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
                    printDifference(now.getTime(), getCal.getTime(), this);
                } else {
                    Logger.d("After");
                    now.add(Calendar.HOUR, getTime(intentValue));
                    Calendar nowNow = Calendar.getInstance();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(intentValue, String.valueOf(now.getTimeInMillis()));
                    editor.apply();
                    intentCall(intentValue, isStartIo);
                    Timer timer = new Timer();
                    TimerTask youtubeTask = new SurveyText(intentValue);
                    timer.schedule(youtubeTask, now.getTime().getTime() - nowNow.getTime().getTime());
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

        Toasty.error(context, "Please wait for " + elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds + " to unlock this survey", Toasty.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class SurveyText extends TimerTask {
        final String text;

        public SurveyText(String text) {
            this.text = text;
        }

        public void run() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SurveyListActivity.this, "SURVEY")
                    .setSmallIcon(R.drawable.ic_info_outline_white_24dp)
                    .setContentTitle("Survey")
                    .setContentText("Your "+text+" is available to complete.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Survey";
                String description = "Your "+text+" is available to complete.";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("SURVEY", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SurveyListActivity.this);
            notificationManager.notify(1234789, builder.build());
            Logger.d("I am in survey timerTask");
        }
    }
}