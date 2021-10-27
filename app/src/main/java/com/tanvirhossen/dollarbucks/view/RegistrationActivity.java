package com.tanvirhossen.dollarbucks.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.api.JsonPlaceHolder;
import com.tanvirhossen.dollarbucks.global.GlobalVals;
import com.tanvirhossen.dollarbucks.model.CountryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity {
    private MaterialButton goToLogin, signUp;
    private EditText email, password, invitationCode;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViewById();
        checkCountry();
        buttonCallBacks();
    }

    private void buttonCallBacks() {
        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        signUp.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || invitationCode.getText().toString().isEmpty()) {
                Toasty.error(this, "Please fill all of the fields", Toast.LENGTH_SHORT, true).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                db.collection("device").document("device").get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.get("device_list") != null) {
                        List<String> devices = (List<String>) documentSnapshot.get("device_list");
                        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        if (!devices.contains(android_id)) { //!check here
                            new AlertDialog.Builder(RegistrationActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("An account is already created on this device")
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                            Logger.e("Already registered");
                        } else {
                            List<String> copyOfDevices = devices;
                            copyOfDevices.add(android_id);
                            Map<String, Object> devicesIds = new HashMap<>();
                            devicesIds.put("device_list", copyOfDevices);
                            db.collection("device").document("device").set(devicesIds, SetOptions.merge())
                                    .addOnSuccessListener(unused -> {
                                        Logger.d("Added Successfully");
                                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                                            if (task.isSuccessful()) {
                                                Logger.d("User successfully created");
                                                if (task.getResult() != null) {
                                                    if (task.getResult().getUser() != null) {
                                                        addValues(task);
                                                    }
                                                }
                                                Toasty.success(this, "Registration Successful", Toasty.LENGTH_SHORT).show();
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean(GlobalVals.login, true);
                                                editor.apply();
                                                startActivity(new Intent(this, MainActivity.class));
                                                finish();
                                            } else {
                                                Logger.e(task.getException() + "");
                                                Toasty.error(this, "Something went wrong. Error:" + task.getException(), Toasty.LENGTH_SHORT).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Logger.e("Failure: " + e.getMessage());
                                        Toasty.error(this, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
            }
        });
    }

    private void addValues(Task<AuthResult> task){
        Map<String, Object> userEmail = new HashMap<>();
        userEmail.put("email", email.getText().toString());
        db.collection("profile").document(task.getResult().getUser().getUid()).set(userEmail, SetOptions.merge()).addOnCompleteListener(task1 -> {
            Logger.d("Added email successfully");
            Map<String, Object> userRefCode = new HashMap<>();
            userRefCode.put("ref", invitationCode.getText().toString());
            db.collection("profile").document(task.getResult().getUser().getUid()).set(userRefCode, SetOptions.merge()).addOnCompleteListener(task2 -> Logger.d("Added ref code successfully"));
        });
    }

    private void checkCountry() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iplist.cc/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<CountryModel> call = jsonPlaceHolderApi.getCountry();
        ArrayList<String> arrayListCountry = new ArrayList<>();
        arrayListCountry.add("AF");
        arrayListCountry.add("AM");
        arrayListCountry.add("AZ");
        arrayListCountry.add("BH");
        arrayListCountry.add("BD");
        arrayListCountry.add("BT");
        arrayListCountry.add("KH");
        arrayListCountry.add("BN");
        arrayListCountry.add("CN");
        arrayListCountry.add("CX");
        arrayListCountry.add("CC");
        arrayListCountry.add("IO");
        arrayListCountry.add("GE");
        arrayListCountry.add("BT");
        arrayListCountry.add("HK");
        arrayListCountry.add("IN");
        arrayListCountry.add("ID");
        arrayListCountry.add("IR");
        arrayListCountry.add("IQ");
        arrayListCountry.add("IL");
        arrayListCountry.add("JP");
        arrayListCountry.add("JO");
        arrayListCountry.add("KZ");
        arrayListCountry.add("KW");
        arrayListCountry.add("KG");
        arrayListCountry.add("LA");
        arrayListCountry.add("LB");
        arrayListCountry.add("MO");
        arrayListCountry.add("MY");
        arrayListCountry.add("MV");
        arrayListCountry.add("MN");
        arrayListCountry.add("MM");
        arrayListCountry.add("NP");
        arrayListCountry.add("KP");
        arrayListCountry.add("OM");
        arrayListCountry.add("PK");
        arrayListCountry.add("PS");
        arrayListCountry.add("PH");
        arrayListCountry.add("QA");
        arrayListCountry.add("SA");
        arrayListCountry.add("SG");
        arrayListCountry.add("LK");
        arrayListCountry.add("BT");

        call.enqueue(new Callback<CountryModel>() {
            @Override
            public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
                if (!arrayListCountry.contains(response.body().getCountrycode())) { //!check here
                    db.collection("notice").document("notice").get().addOnSuccessListener(documentSnapshot -> {
                        boolean notice = documentSnapshot.getBoolean("notice");
                        if (notice) {
                            new AlertDialog.Builder(RegistrationActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("You are an outsider")
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                    //Toasty.error(RegistrationActivity.this, "You are not eligible", Toasty.LENGTH_SHORT).show();
                } else {
                    //Toasty.success(RegistrationActivity.this, "You are eligible", Toasty.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<CountryModel> call, Throwable t) {

            }
        });
    }

    private void findViewById() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(GlobalVals.sharedPrefName, MODE_PRIVATE);
        Logger.addLogAdapter(new AndroidLogAdapter());
        goToLogin = findViewById(R.id.go_to_login);
        email = findViewById(R.id.registration_email);
        password = findViewById(R.id.registration_password);
        invitationCode = findViewById(R.id.registration_invitation);
        signUp = findViewById(R.id.registration_sign_up);
        progressBar = findViewById(R.id.progress_reg);
    }
}