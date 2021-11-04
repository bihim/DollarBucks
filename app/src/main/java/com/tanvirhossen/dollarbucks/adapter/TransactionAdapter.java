package com.tanvirhossen.dollarbucks.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.model.TransactionModel;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final ArrayList<TransactionModel> transactionModelArrayList;
    private final Context context;

    public TransactionAdapter(ArrayList<TransactionModel> transactionModelArrayList, Context context) {
        this.transactionModelArrayList = transactionModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel selectedItem = transactionModelArrayList.get(position);
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.d(selectedItem.isPending());
        holder.textViewStatus.setText(selectedItem.isPending() ? "Pending" : "Paid");
        holder.textViewPayment.setText(selectedItem.getTransactionMethod());
        holder.linearLayout.setBackgroundTintList(selectedItem.isPending()? ColorStateList.valueOf(context.getResources().getColor(R.color.today_user_color)): ColorStateList.valueOf(context.getResources().getColor(R.color.online_color)));
        holder.transactionMoney.setText(getMoney(selectedItem.getTransactionMethod()));
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textViewPayment;
        TextView textViewStatus, transactionMoney;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.transaction_status_color);
            textViewPayment = itemView.findViewById(R.id.transaction_method);
            textViewStatus = itemView.findViewById(R.id.transaction_status);
            transactionMoney = itemView.findViewById(R.id.transaction_money);
        }
    }

    private String getMoney(String transactionType){
        switch (transactionType) {
            case "Paytm":
                return "6";
            case "Bkash":
                return "4";
            case "Nagad":
                return "5";
            default:
                return "8";
        }
    }
}
