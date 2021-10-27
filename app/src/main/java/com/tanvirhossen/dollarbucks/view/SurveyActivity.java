package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.adapter.SurveyAdapter;
import com.tanvirhossen.dollarbucks.model.SurveyModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SurveyActivity extends AppCompatActivity {

    private ArrayList<SurveyModel> surveyModelArrayList;
    private SurveyAdapter surveyAdapter;
    private TextView textViewToolbar;
    private TextView textViewQuestions;
    private RecyclerView recyclerView;
    private MaterialToolbar materialToolbar;
    private ArrayList<String> questionsArraylist;
    private ImageButton imageButtonBack;
    private ProgressBar progressBar;
    private int initialIndex = 0;
    private int count = 0;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        findViewById();
        setSupportActionBar(materialToolbar);
        //setupSurveyModel();
        String intentValue = getIntent().getStringExtra("survey");
        getAllQuestions(intentValue);
        imageButtonBack.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void getAllQuestions(String docPath){
        db.collection("survey").document(docPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    surveyModelArrayList = new ArrayList<>();
                    Map<String, Object> map = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()){
                        Logger.d(entry.getKey());
                        ArrayList<String> answers = (ArrayList<String>) entry.getValue();
                        StringBuilder answerBuilder = new StringBuilder();
                        for(String answer: answers){
                            answerBuilder.append(answer);
                            answerBuilder.append(",");
                        }
                        surveyModelArrayList.add(new SurveyModel(1, entry.getKey(), answerBuilder.toString()));
                        setupRecyclerview(surveyModelArrayList);
                        Logger.d(answers);
                    }

                }

            }
        });
    }

    private void setupRecyclerview(ArrayList<SurveyModel> surveyModelArrayList) {
        String topQuestion = surveyModelArrayList.get(initialIndex).getQuestion();
        String questionList = surveyModelArrayList.get(initialIndex).getOption();
        String[] questions = questionList.split(",");
        questionsArraylist = new ArrayList<>();
        questionsArraylist.addAll(Arrays.asList(questions));
        textViewQuestions.setText(topQuestion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        surveyAdapter = new SurveyAdapter(questionsArraylist, this);
        recyclerView.setAdapter(surveyAdapter);
        textViewToolbar.setText((initialIndex + 1) + " Out of " + surveyModelArrayList.size());
        surveyAdapter.setOnItemClickListener(position -> {
            String topQuestionUpdated = surveyModelArrayList.get(initialIndex).getQuestion();
            String questionListUpdated = surveyModelArrayList.get(initialIndex).getOption();
            String[] questionsUpdated = questionListUpdated.split(",");
            Logger.d("Questionssssss " + questionsUpdated[position]);
            Date c = Calendar.getInstance().getTime();
            progressBar.setVisibility(View.VISIBLE);
            //System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            Map<String, Object> data = new HashMap<>();
            data.put(topQuestionUpdated, questionsUpdated[position]);

            db.collection("profile").document(firebaseAuth.getUid()).collection("survey") //here sports is dynamic
                    .document("sports").collection("date").
                    document(formattedDate).set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Logger.d("Added Successfully");
                        progressBar.setVisibility(View.GONE);
                    })
            .addOnFailureListener(e -> {
                Logger.e("Failure: "+e.getMessage());
                progressBar.setVisibility(View.GONE);
                Toasty.error(this, "Error: "+e.getMessage(), Toasty.LENGTH_SHORT).show();
            });

            if (surveyModelArrayList.size() > initialIndex + 1) {
                //Logger.d(initialIndex);
                showDialog(this);
                questionsArraylist.clear();
                textViewToolbar.setText((initialIndex + 1) + " Out of " + surveyModelArrayList.size());
                String topQuestions = surveyModelArrayList.get(initialIndex).getQuestion();
                textViewQuestions.setText(topQuestions);
                String questionLists = surveyModelArrayList.get(initialIndex).getOption();
                String[] questionsInc = questionLists.split(",");
                //Logger.d("Arrays of question: "+Arrays.toString(questionsInc));
                questionsArraylist.addAll(Arrays.asList(questionsInc));
                //Logger.d("Selected Question: "+questionsArraylist.get(position));
                surveyAdapter.notifyDataSetChanged();
            } else {
                showDialogFinish(this);
                Logger.e("I am at else");
            }
        });
    }

    private void setupSurveyModel() {
        surveyModelArrayList = new ArrayList<>();
        surveyModelArrayList.add(new SurveyModel(1, "What is your favorite os?", "Linux,Windows,MacOS"));
        surveyModelArrayList.add(new SurveyModel(2, "What is your favorite framework?", "React,Flutter,Android"));
        surveyModelArrayList.add(new SurveyModel(3, "What is your favorite hearing system?", "Loud,Speaker,Headphone"));
        surveyModelArrayList.add(new SurveyModel(4, "What is your favorite country?", "Bangladesh,India,Pakistan"));
        surveyModelArrayList.add(new SurveyModel(5, "What is your favorite food?", "Noodles,Kacchi,Biriani"));
        surveyModelArrayList.add(new SurveyModel(6, "What is your favorite pet?", "Cat,Dog,Parrot"));
        surveyModelArrayList.add(new SurveyModel(7, "What is your favorite healthy food?", "Rice,Spinach,Egg"));
        surveyModelArrayList.add(new SurveyModel(8, "What is your favorite person?", "Father,Mother,Wife"));
    }

    private void findViewById() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        materialToolbar = findViewById(R.id.survey_toolbar);
        textViewToolbar = findViewById(R.id.survey_toolbar_text);
        textViewQuestions = findViewById(R.id.survey_questions);
        recyclerView = findViewById(R.id.survey_recyclerview);
        progressBar = findViewById(R.id.survey_progress);
        imageButtonBack = findViewById(R.id.imageButton);
    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_survey_ad);

        MaterialButton materialButtonAd = dialog.findViewById(R.id.dialog_ad);
        MaterialButton materialButtonSkip = dialog.findViewById(R.id.dialog_skip);
        initialIndex++;
        materialButtonAd.setOnClickListener(v -> {
            Toasty.info(activity, "Ad showing", Toasty.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        materialButtonSkip.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public void showDialogFinish(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_survey_ad);

        MaterialButton materialButtonAd = dialog.findViewById(R.id.dialog_ad);
        MaterialButton materialButtonSkip = dialog.findViewById(R.id.dialog_skip);
        materialButtonAd.setOnClickListener(v -> {
            Toasty.info(activity, "Ad showing", Toasty.LENGTH_SHORT).show();
            startActivity(new Intent(activity, SurveyFinishActivity.class));
            dialog.dismiss();

        });

        materialButtonSkip.setOnClickListener(v -> {
            startActivity(new Intent(activity, SurveyFinishActivity.class));
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}