package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextView points, balance, pending, username;
    private FirebaseAuth firebaseAuth;
    private MaterialButton materialButtonPointsToDollar;
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
                    textViewPointsToDollar.setText("10000 = $0.10\n"+task.getResult().get("balance").toString());
                    pending.setText("Pending: " + task.getResult().get("pending").toString());
                }
            }
        });
    }

    private void setButtonCallBacks() {
        materialButtonPointsToDollar.setOnClickListener(v -> {
            convertPointToDollars();
        });
    }

    private void convertPointToDollars() {
        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                double balance = Double.parseDouble(task.getResult().get("balance").toString());
                int points = Integer.parseInt(task.getResult().get("points").toString());
                if (points >= 1000) {
                    int getPoints = points - 1000;
                    double getBalance = balance + 0.01;
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
    }
}