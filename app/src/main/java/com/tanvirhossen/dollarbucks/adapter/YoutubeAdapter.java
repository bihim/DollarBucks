package com.tanvirhossen.dollarbucks.adapter;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.model.YoutubeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        holder.textViewCPC.setText(selectedModel.getCpc());
        holder.itemView.setOnClickListener(v -> {
            Logger.d("Calling intent!!");
            /*Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(selectedModel.getUrl()));
            intent.setComponent(new ComponentName("com.google.android.youtube", "com.google.android.youtube.PlayerActivity"));

            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
            if (infos.size() > 0) {
                context.startActivity(intent);
            } else {
                //No Application can handle your intent
            }*/
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(selectedModel.getUrl()));
            intent.setPackage("com.google.android.youtube");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return youtubeModels.size();
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


}
