package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class InvitationCodeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    private MaterialButton materialButtonGenerate, materialButtonApply;
    private TextView textViewInvitation;
    private EditText editTextInputInvite;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_code);
        findViewById();
        buttonCallBacks();
    }

    private void buttonCallBacks() {
        materialButtonGenerate.setOnClickListener(v -> {
            db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String refCode = task.getResult().getString("ref");
                    if (refCode.equals("") || refCode.equals("1212")) {
                        generateInvitationCode();
                    } else {
                        textViewInvitation.setText(refCode);
                    }
                }
            });

        });
        materialButtonApply.setOnClickListener(v -> {
            if (editTextInputInvite.getText().toString().isEmpty()) {
                Toasty.error(InvitationCodeActivity.this, "Input Invite Code", Toasty.LENGTH_SHORT).show();
            } else {
                applyCode(editTextInputInvite.getText().toString());
            }
        });
    }

    private void applyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("invitecode").document(code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            String provider = task.getResult().getString("provider");
                            if (!provider.equals(firebaseAuth.getCurrentUser().getUid())) {
                                boolean isApplied = task.getResult().getBoolean("isApplied");
                                if (isApplied) {
                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(InvitationCodeActivity.this, "This promo code is already applied", Toasty.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("applier", firebaseAuth.getCurrentUser().getUid());
                                    map.put("isApplied", true);
                                    db.collection("invitecode").document(code).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            getBalanceThenApply(provider);
                                            //getBalanceThenApply(firebaseAuth.getCurrentUser().getUid());
                                        }
                                    });
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toasty.error(InvitationCodeActivity.this, "You can not apply your promo code", Toasty.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toasty.error(InvitationCodeActivity.this, "Wrong invitation code", Toasty.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(InvitationCodeActivity.this, "Wrong invitation code", Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getBalanceThenApply(String uid) {
        db.collection("profile").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                double getBalance = Double.parseDouble(task.getResult().getString("balance"));
                getBalance = getBalance + 0.02;
                Map<String, Object> setMoney = new HashMap<>();
                setMoney.put("balance", String.format("%.2f", getBalance));
                db.collection("profile").document(uid).set(setMoney, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toasty.success(InvitationCodeActivity.this, "Promo code applied", Toasty.LENGTH_SHORT).show();
                        Logger.d("Added Money");
                    }
                });
            }
        });
    }

    private void generateInvitationCode() {
        progressBar.setVisibility(View.VISIBLE);
        int min = 10000;
        int max = 99999;
        db.collection("invitecode").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Get all documents
                //add them on arraylist
                ArrayList<Integer> docLists = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    docLists.add(Integer.parseInt(documentSnapshot.getId()));
                }
                //get a random number
                Random r = new Random();
                int random = r.nextInt(max - min + 1) + min;
                //check if it is available
                if (!docLists.contains(random)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("provider", firebaseAuth.getCurrentUser().getUid());
                    map.put("applier", "");
                    map.put("isApplied", false);
                    db.collection("invitecode").document(String.valueOf(random)).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Map<String, Object> mapSet = new HashMap<>();
                            mapSet.put("ref", String.valueOf(random));
                            db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(mapSet, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    textViewInvitation.setText(String.valueOf(random));
                                }
                            });
                        }
                    });

                } else {
                    Toasty.error(InvitationCodeActivity.this, "Try again", Toasty.LENGTH_SHORT).show();
                }
                //generate one if not
                //assign applier, provider and isApplied
                //
            }
        });
    }

    private void findViewById() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        materialButtonGenerate = findViewById(R.id.generate_invitation);
        materialButtonApply = findViewById(R.id.apply_invitation);
        textViewInvitation = findViewById(R.id.invitation_code);
        editTextInputInvite = findViewById(R.id.registration_email);
        progressBar = findViewById(R.id.progress_bar_invite);
    }
}