package com.tanvirhossen.dollarbucks.view;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.startapp.sdk.adsbase.StartAppAd;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PDFReaderActivity extends AppCompatActivity {

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    private boolean isBackPressedEnabled = false;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        StartAppAd.showAd(this);
        TextView textView = findViewById(R.id.pdfTop);
        TextView textView1 = findViewById(R.id.pdfStory);
        TextView timer = findViewById(R.id.timer);

        String top = getIntent().getStringExtra("top");
        String story = getIntent().getStringExtra("story");

        textView.setText(top);
        textView1.setText(story);

        interstitialAd = new MaxInterstitialAd(GlobalVals.applovinInterstatial, this);
        interstitialAd.loadAd();
        interstitialAd.showAd();

        new CountDownTimer(20 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                isBackPressedEnabled = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(PDFReaderActivity.this)
                        .setTitle("Success")
                        .setMessage("You have got 10c")
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Collect", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                builder.show();
                firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        int points = Integer.parseInt(task.getResult().get("points").toString());
                        int getPoints = points + 10;
                        Map<String, Object> updatedBalance = new HashMap<>();
                        updatedBalance.put("points", String.valueOf(getPoints));
                        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(updatedBalance, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        if (isBackPressedEnabled) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Wait for time to complete", Toast.LENGTH_SHORT).show();
        }
    }
}