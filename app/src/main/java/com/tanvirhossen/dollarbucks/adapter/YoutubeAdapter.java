package com.tanvirhossen.dollarbucks.adapter;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.global.GlobalVals;
import com.tanvirhossen.dollarbucks.model.YoutubeModel;
import com.tanvirhossen.dollarbucks.view.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder> {
    final ArrayList<YoutubeModel> youtubeModels;
    final Context context;

    public YoutubeAdapter(ArrayList<YoutubeModel> youtubeModels, Context context) {
        this.youtubeModels = youtubeModels;
        this.context = context;
    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_youtube, parent, false);
        return new YoutubeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {
        Logger.addLogAdapter(new AndroidLogAdapter());
        YoutubeModel selectedModel = youtubeModels.get(position);
        holder.textViewDuration.setText(selectedModel.getTime());
        holder.textViewCPC.setText(selectedModel.getCpc() + "$");
        holder.itemView.setOnClickListener(v -> {
            //Logger.d("Calling intent!!");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


            db.collection("timer").document("timer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    int timeInSec = Integer.parseInt(task.getResult().getString("tv"));
                    if (!sharedPreferences.contains(selectedModel.getUrl())) {
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.SECOND, timeInSec);

                        Calendar nowNow = Calendar.getInstance();
                        Date afterAdd = now.getTime();
                        Date nowAdd = nowNow.getTime();

                        Timer timer = new Timer();
                        TimerTask youtubeTask = new YoutubeTask();
                        timer.schedule(youtubeTask, afterAdd.getTime() - nowAdd.getTime());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(selectedModel.getUrl(), String.valueOf(now.getTimeInMillis()));
                        editor.apply();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Warning")
                                .setMessage("Wait 5 seconds to get reward")
                                .setCancelable(true)
                                .setIcon(android.R.drawable.ic_dialog_alert);
                        AlertDialog alertDialog = builder.create();

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(selectedModel.getUrl()));
                        intent.setPackage("com.google.android.youtube");
                        context.startActivity(intent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.show();
                            }
                        }, 1000);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                AlertDialog.Builder alertDialogBuilderYoutube = new AlertDialog.Builder(context)
                                        .setTitle("Success")
                                        .setMessage("You have got 0.02$")
                                        .setCancelable(true)
                                        .setIcon(android.R.drawable.ic_menu_upload_you_tube);
                                AlertDialog alertDialogYoutube = alertDialogBuilderYoutube.create();
                                alertDialogYoutube.show();
                                db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            double balance = Double.parseDouble(task.getResult().getString("balance"));
                                            balance = balance + Double.parseDouble(selectedModel.getCpc());
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("balance", String.format("%.2f", balance));
                                            db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Logger.d("Balance Added");
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }, 5000);


                        Logger.d("Data Inserted Sharedpref");
                    } else {
                        String getTime = sharedPreferences.getString(selectedModel.getUrl(), "null");
                        if (!getTime.equals("null")) {
                            long getTimeInMillis = Long.parseLong(getTime);
                            Calendar getCal = Calendar.getInstance();
                            getCal.setTimeInMillis(getTimeInMillis);
                            Calendar now = Calendar.getInstance();
                            if (now.before(getCal)) {
                                long remaining = getCal.getTimeInMillis() - now.getTimeInMillis();
                                Calendar calendarRemaining = Calendar.getInstance();
                                calendarRemaining.setTimeInMillis(remaining);
                                Timer timer = new Timer();
                                TimerTask youtubeTask = new YoutubeTask();
                                timer.schedule(youtubeTask, getCal.getTime().getTime() - now.getTime().getTime());
                                printDifference(now.getTime(), getCal.getTime(), context);
                            } else {
                                Logger.d("After");
                                now.add(Calendar.SECOND, timeInSec);
                                Calendar nowNow = Calendar.getInstance();
                                Date afterAdd = now.getTime();
                                Date nowAdd = nowNow.getTime();

                                Timer timer = new Timer();
                                TimerTask youtubeTask = new YoutubeTask();
                                timer.schedule(youtubeTask, afterAdd.getTime() - nowAdd.getTime());

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(selectedModel.getUrl(), String.valueOf(now.getTimeInMillis()));
                                editor.apply();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("Warning")
                                        .setMessage("Wait 5 seconds to get reward")
                                        .setCancelable(true)
                                        .setIcon(android.R.drawable.ic_dialog_alert);
                                AlertDialog alertDialog = builder.create();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.show();
                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilderYoutube = new AlertDialog.Builder(context)
                                                .setTitle("Success")
                                                .setMessage("You have got 0.02$")
                                                .setCancelable(true)
                                                .setIcon(android.R.drawable.ic_menu_upload_you_tube);
                                        AlertDialog alertDialogYoutube = alertDialogBuilderYoutube.create();
                                        alertDialogYoutube.show();
                                        db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    double balance = Double.parseDouble(task.getResult().getString("balance"));
                                                    balance = balance + Double.parseDouble(selectedModel.getCpc());
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("balance", String.format("%.2f", balance));
                                                    db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Logger.d("Balance Added");
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                }, 5000);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(selectedModel.getUrl()));
                                intent.setPackage("com.google.android.youtube");
                                context.startActivity(intent);

                                /*db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            double balance = Double.parseDouble(task.getResult().getString("balance"));
                                            balance = balance + Double.parseDouble(selectedModel.getCpc());
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("balance", String.format("%.2f", balance));
                                            db.collection("profile").document(firebaseAuth.getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(selectedModel.getUrl()));
                                                    intent.setPackage("com.google.android.youtube");
                                                    context.startActivity(intent);
                                                    Logger.d("Balance Added");
                                                }
                                            });
                                        }
                                    }
                                });*/
                                //((Activity) context).finish();
                            }
                        }
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return youtubeModels.size();
    }

    private void printDifference(Date startDate, Date endDate, Context context) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Toasty.error(context, "Please wait for " + elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds + " to unlock this video", Toasty.LENGTH_SHORT).show();
    }

    class YoutubeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCPC;
        TextView textViewDuration;

        public YoutubeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCPC = itemView.findViewById(R.id.cpc);
            textViewDuration = itemView.findViewById(R.id.duration);
        }
    }

    class YoutubeTask extends TimerTask {
        public void run() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "YOUTUBE")
                    .setSmallIcon(R.drawable.ic_youtube)
                    .setContentTitle("TV")
                    .setContentText("Your tv is available to watch")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "TV";
                String description = "Your tv is available to watch";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("YOUTUBE", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1234567, builder.build());
            Logger.d("I am in youtube timerTask");
        }
    }

}
