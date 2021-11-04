package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextView points, balance, pending, username;
    private FirebaseAuth firebaseAuth;
    private MaterialButton materialButtonPointsToDollar, payment, transaction, materialButtonLogout, proof, invite;
    private TextView textViewPointsToDollar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById();
        setButtonCallBacks();
        setValues();
    }

    private void setValues() {
        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Logger.d(firebaseAuth.getCurrentUser().getUid());
                if (task.isSuccessful()) {
                    username.setText(task.getResult().get("name").toString());
                    points.setText("Points: " + task.getResult().get("points").toString());
                    balance.setText("Balance: " + task.getResult().get("balance").toString());
                    textViewPointsToDollar.setText("10000 = $0.10\n" + task.getResult().get("balance").toString());
                    pending.setText("Pending: " + task.getResult().get("pending").toString());
                }
            }
        });
    }

    private void setButtonCallBacks() {
        invite.setOnClickListener(v -> startActivity(new Intent(this, InvitationCodeActivity.class)));
        materialButtonPointsToDollar.setOnClickListener(v -> convertPointToDollars());
        payment.setOnClickListener(v -> startActivity(new Intent(this, WithdrawActivity.class)));
        transaction.setOnClickListener(v -> startActivity(new Intent(this, TransactionActivity.class)));
        materialButtonLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(GlobalVals.login, false);
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SplashScreenActivity.class));
            finishAffinity();
        });
        proof.setOnClickListener(v -> {
            startActivity(new Intent(this, ProofActivity.class));
        });
    }

    private void convertPointToDollars() {
        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                double balance = Double.parseDouble(task.getResult().get("balance").toString());
                int points = Integer.parseInt(task.getResult().get("points").toString());
                if (points >= 10000) {
                    int getPoints = points - 10000;
                    double getBalance = balance + 0.1;
                    Map<String, Object> updatedBalance = new HashMap<>();
                    updatedBalance.put("points", String.valueOf(getPoints));
                    updatedBalance.put("balance", String.format("%.2f", getBalance));
                    Logger.d(updatedBalance);
                    materialButtonPointsToDollar.setEnabled(false);
                    firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(updatedBalance, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            materialButtonPointsToDollar.setEnabled(true);
                            setValues();
                            Toasty.success(ProfileActivity.this, "Successfully added balance", Toasty.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toasty.error(ProfileActivity.this, "Can not deduct any more points to balance", Toasty.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    private void findViewById() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        points = findViewById(R.id.profile_points);
        balance = findViewById(R.id.profile_balance);
        pending = findViewById(R.id.profile_pending);
        username = findViewById(R.id.profile_name);
        materialButtonPointsToDollar = findViewById(R.id.points_to_dollar_button);
        textViewPointsToDollar = findViewById(R.id.points_to_dollar_text);
        payment = findViewById(R.id.profile_payment);
        transaction = findViewById(R.id.transaction);
        materialButtonLogout = findViewById(R.id.logout);
        proof = findViewById(R.id.proof);
        invite = findViewById(R.id.invite);
    }
}