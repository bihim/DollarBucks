package com.tanvirhossen.dollarbucks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanvirhossen.dollarbucks.R;
import com.tanvirhossen.dollarbucks.model.SurveyModel;

import java.util.ArrayList;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {

    private final ArrayList<String> surveyModelArrayList;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public SurveyAdapter(ArrayList<String> surveyModelArrayList, Context context) {
        this.surveyModelArrayList = surveyModelArrayList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_survey, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        holder.textView.setText(surveyModelArrayList.get(position));
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.button_color));
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return surveyModelArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class SurveyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textView;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.survey_selected_color);
            textView = itemView.findViewById(R.id.survey_text);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                        if (position == getAdapterPosition()){
                            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.button_color));
                        }
                        else{
                            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                        }

                    }
                }
            });
        }
    }
}
