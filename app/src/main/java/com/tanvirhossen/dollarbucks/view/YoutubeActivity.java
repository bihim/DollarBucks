package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.adapter.YoutubeAdapter;
import com.tanvirhossen.dollarbucks.model.YoutubeModel;

import java.util.ArrayList;

public class YoutubeActivity extends AppCompatActivity {
    private ImageButton imageButtonBack;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<YoutubeModel> youtubeModelArrayList;
    private RecyclerView recyclerView;
    private YoutubeAdapter youtubeAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        findViewById();
        buttonCallBacks();
        setRecyclerView();
    }

    private void setRecyclerView(){
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("youtube").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    youtubeModelArrayList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){
                        String url = documentSnapshot.get("url").toString();
                        String time = documentSnapshot.get("time").toString();
                        String cpc = documentSnapshot.get("cpc").toString();
                        Logger.d("url: "+url+" time: "+time+" cpc: "+cpc);
                        youtubeModelArrayList.add(new YoutubeModel(url, time, cpc));
                    }
                    youtubeAdapter = new YoutubeAdapter(youtubeModelArrayList, YoutubeActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(YoutubeActivity.this));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(youtubeAdapter);
                }
            }
        });
    }

    private void buttonCallBacks(){
        imageButtonBack.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void findViewById(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        Logger.addLogAdapter(new AndroidLogAdapter());
        imageButtonBack = findViewById(R.id.youtube_back);
        recyclerView = findViewById(R.id.youtube_recyclerview);
        progressBar = findViewById(R.id.youtube_load);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}