package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class WithdrawSendActivity extends AppCompatActivity {
    private int position;
    private String transactionName;
    private String transactionMoney;
    private int resourceId;

    private ImageView imageView;
    private TextView textViewName, textViewPaymentType, textViewTopName;
    private TextInputEditText textInputEditText;
    private ProgressBar progressBar;
    private MaterialButton materialButton;
    private ImageButton imageButton;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_send);
        position = getIntent().getIntExtra("position", 0);
        resourceId = getIntent().getIntExtra("image", R.drawable.bkash);
        transactionName = getTransactionName(position);
        transactionMoney = getTransactionMoney(position);
        Logger.addLogAdapter(new AndroidLogAdapter());
        findViewById();
        setResources();
        setButtonCallbacks();
    }

    private void setButtonCallbacks() {

        imageButton.setOnClickListener(v -> {
            onBackPressed();
        });

        materialButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            firebaseFirestore.collection("withdraw").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> docList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        docList.add(document.getId());
                    }
                    Logger.d(docList);
                    if (docList.contains(firebaseAuth.getCurrentUser().getUid())) {
                        Logger.d("I am at if");
                        firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Integer> arrayList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        arrayList.add(Integer.parseInt(document.getId()));
                                    }
                                    Logger.d("Arraylist number: " + arrayList);
                                    //int largestNumber = getLargest(arrayList);
                                    int largestNumber = Collections.max(arrayList);
                                    Logger.d(largestNumber);
                                    String description = textInputEditText.getText().toString();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("amount", transactionMoney);
                                    data.put("description", description);
                                    /*data.put("isPending", false);
                                    data.put("payment_type", transactionName);*/
                                    firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").document(String.valueOf(largestNumber + 1)).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> data2 = new HashMap<>();
                                                data2.put("isPending", true);
                                                data2.put("payment_type", transactionName);
                                                firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").document(String.valueOf(largestNumber + 1)).set(data2, SetOptions.merge()).addOnCompleteListener(task1 -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task1.isSuccessful()) {
                                                        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                int pending = Integer.parseInt(task.getResult().getString("pending"));
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("pending", String.valueOf(pending + 1));
                                                                firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        onBackPressed();
                                                                        Toasty.success(WithdrawSendActivity.this, "Your request is submitted. Wait for approval", Toasty.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        /*onBackPressed();
                                                        Toasty.success(WithdrawSendActivity.this, "Your request is submitted. Wait for approval", Toasty.LENGTH_SHORT).show();*/
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toasty.error(WithdrawSendActivity.this, "Something went wrong", Toasty.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        });
                    } else {
                        Logger.d("I am at else");
                        String description = textInputEditText.getText().toString();
                        Map<String, Object> data = new HashMap<>();
                        data.put("amount", transactionMoney);
                        data.put("description", description);
                        firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").document("1").set(data, SetOptions.merge()).addOnCompleteListener(task12 -> {
                            progressBar.setVisibility(View.GONE);
                            if (task12.isSuccessful()) {
                                Map<String, Object> data2 = new HashMap<>();
                                data2.put("isPending", true);
                                data2.put("payment_type", transactionName);
                                firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").document("1").set(data2, SetOptions.merge()).addOnCompleteListener(task121 -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (task121.isSuccessful()) {
                                        Map<String, Object> datas = new HashMap<>();
                                        datas.put("dummy", "dummy");
                                        firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).set(datas).addOnCompleteListener(task1211 -> {
                                            firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    int pending = Integer.parseInt(task.getResult().getString("pending"));
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("pending", String.valueOf(pending + 1));
                                                    firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            onBackPressed();
                                                            Toasty.success(WithdrawSendActivity.this, "Your request is submitted. Wait for approval", Toasty.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                        });

                                    }
                                });
                                Toasty.success(WithdrawSendActivity.this, "Your request is submitted. Wait for approval", Toasty.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toasty.error(WithdrawSendActivity.this, "Something went wrong", Toasty.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });
    }

    private void setResources() {
        imageView.setImageResource(resourceId);
        textViewPaymentType.setText(transactionName);
        textViewTopName.setText("Amount: $"+transactionMoney);
        firebaseFirestore.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            Logger.d(firebaseAuth.getCurrentUser().getUid());
            if (task.isSuccessful()) {
                textViewName.setText(task.getResult().get("name").toString());
            }
        });
    }

    private void findViewById() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.withdraw_image);
        textViewName = findViewById(R.id.withdraw_name);
        textInputEditText = findViewById(R.id.withdraw_desc);
        progressBar = findViewById(R.id.withdraw_progress);
        materialButton = findViewById(R.id.withdraw_submit);
        textViewPaymentType = findViewById(R.id.withdraw_payment_type);
        imageButton = findViewById(R.id.imageButton);
        textViewTopName = findViewById(R.id.withdraw_top_name);
    }

    private String getTransactionName(int position) {
        switch (position) {
            case 0:
                return "Paytm";
            case 1:
                return "Nagad";
            case 2:
                return "Bkash";
            case 3:
                return "Jazz";
            case 4:
                return "Gcash";
            default:
                return "Paypal";
        }
    }

    private String getTransactionMoney(int position) {
        switch (position) {
            case 0:
                return "6";
            case 1:
                return "5";
            case 2:
                return "4";
            default:
                return "8";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}