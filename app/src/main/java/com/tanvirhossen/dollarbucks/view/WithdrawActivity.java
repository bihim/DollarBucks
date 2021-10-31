package com.tanvirhossen.dollarbucks.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.adapter.WithdrawAdapter;

import java.util.ArrayList;

public class WithdrawActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Integer> withdrawList;
    private ProgressBar progressBar;
    private WithdrawAdapter withdrawAdapter;
    private ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        findViewById();
        setImageButtonBack();
        setRecyclerView();
    }

    private void setRecyclerView(){
        withdrawList = new ArrayList<>();
        withdrawList.add(R.drawable.paytm);
        withdrawList.add(R.drawable.nagad);
        withdrawList.add(R.drawable.bkash);
        withdrawList.add(R.drawable.jazz);
        withdrawList.add(R.drawable.gcash);
        withdrawList.add(R.drawable.paypal);
        withdrawAdapter = new WithdrawAdapter(withdrawList, this);
        recyclerView.setAdapter(withdrawAdapter);
    }

    private void setImageButtonBack(){
        imageButtonBack.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void findViewById(){
        recyclerView = findViewById(R.id.withdraw_recyclerview);
        progressBar = findViewById(R.id.withdraw_loading);
        imageButtonBack = findViewById(R.id.imageButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}