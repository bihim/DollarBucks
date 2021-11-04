package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.adapter.TransactionAdapter;
import com.tanvirhossen.dollarbucks.model.TransactionModel;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private ImageButton imageButton;

    //Firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        findViewById();
        imageButton.setOnClickListener(v -> {
            onBackPressed();
        });
        setRecyclerView();
    }

    private void setRecyclerView() {
        firebaseFirestore.collection("withdraw").document(firebaseAuth.getCurrentUser().getUid()).collection("payment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    transactionModelArrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        boolean isPending = queryDocumentSnapshot.getBoolean("isPending");
                        //Logger.d(isPending);
                        String paymentType = queryDocumentSnapshot.getString("payment_type");
                        transactionModelArrayList.add(new TransactionModel(paymentType, isPending));
                    }
                    transactionAdapter = new TransactionAdapter(transactionModelArrayList, TransactionActivity.this);
                    recyclerView.setAdapter(transactionAdapter);
                }
            }
        });
    }

    private void findViewById() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        recyclerView = findViewById(R.id.transaction_recyclerview);
        imageButton = findViewById(R.id.imageButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}