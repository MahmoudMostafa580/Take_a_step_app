package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeastep.R;
import com.example.takeastep.models.Vaccine;

import java.util.ArrayList;

public class TogetherWeWinAdapter extends RecyclerView.Adapter<TogetherWeWinAdapter.TogetherWeWinViewHolder> {
    private ArrayList<Vaccine> mVaccine;
    private Context mContext;

    public TogetherWeWinAdapter(ArrayList<Vaccine> mVaccine, Context mContext) {
        this.mVaccine = mVaccine;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TogetherWeWinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TogetherWeWinViewHolder(LayoutInflater.from(mContext).inflate(R.layout.together_we_win_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TogetherWeWinViewHolder holder, int position) {
        Vaccine currentVaccine = mVaccine.get(position);
        holder.vaccineInfo.setText(currentVaccine.getInfo());
        holder.vaccineImage.setImageURI(Uri.parse(currentVaccine.getImage()));
        Glide.with(mContext).load(Uri.parse(currentVaccine.getImage())).into(holder.vaccineImage);
    }

    @Override
    public int getItemCount() {
        return mVaccine.size();
    }

    public static class TogetherWeWinViewHolder extends RecyclerView.ViewHolder {
        TextView vaccineInfo;
        ImageView vaccineImage;

        public TogetherWeWinViewHolder(@NonNull View itemView) {
            super(itemView);
            vaccineInfo=itemView.findViewById(R.id.vaccineInfo);
            vaccineImage=itemView.findViewById(R.id.vaccineImage);
        }
    }
}
