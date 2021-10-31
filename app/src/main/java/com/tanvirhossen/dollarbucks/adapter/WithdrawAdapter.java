package com.tanvirhossen.dollarbucks.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.view.WithdrawSendActivity;

import java.util.ArrayList;

public class WithdrawAdapter extends  RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder> {
    private final ArrayList<Integer> withdrawList;
    private final Context context;

    public WithdrawAdapter(ArrayList<Integer> withdrawList, Context context) {
        this.withdrawList = withdrawList;
        this.context = context;
    }

    @NonNull
    @Override
    public WithdrawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_withdraw, parent, false);
        return new WithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawViewHolder holder, int position) {
        holder.imageView.setImageResource(withdrawList.get(position));
        holder.materialButton.setOnClickListener(v->{
            Logger.addLogAdapter(new AndroidLogAdapter());
            Logger.d("This is "+position);
            Intent intent = new Intent(context, WithdrawSendActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("image", withdrawList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return withdrawList.size();
    }

    public class WithdrawViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialButton materialButton;
        public WithdrawViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.withdraw_logo);
            materialButton = itemView.findViewById(R.id.withdraw_button);
        }
    }
}
