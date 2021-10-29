package com.tanvirhossen.dollarbucks.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tanvirhossen.dollarbucks.R;

import java.util.ArrayList;

public class ProofAdapter extends RecyclerView.Adapter<ProofAdapter.ProofViewHolder> {
    private final ArrayList<String> proofImageList;
    private final Context context;

    public ProofAdapter(ArrayList<String> proofImageList, Context context) {
        this.proofImageList = proofImageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProofViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_proof, parent, false);
        return new ProofViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProofViewHolder holder, int position) {
        setExistImage(holder.imageView, proofImageList.get(position));

    }

    @Override
    public int getItemCount() {
        return proofImageList.size();
    }

    private void setExistImage(ImageView imageView, String base64String) {
        if (!base64String.isEmpty()) {
            byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
    }

    public class ProofViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ProofViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.proof_image);
        }
    }
}
