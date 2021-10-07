package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.vaccineName.setText(currentVaccine.getName());
        holder.vaccineInfo.setText(currentVaccine.getInfo());
    }

    @Override
    public int getItemCount() {
        return mVaccine.size();
    }

    public static class TogetherWeWinViewHolder extends RecyclerView.ViewHolder {
        TextView vaccineName,vaccineInfo;

        public TogetherWeWinViewHolder(@NonNull View itemView) {
            super(itemView);
            vaccineName=itemView.findViewById(R.id.vaccine_name);
            vaccineInfo=itemView.findViewById(R.id.vaccine_info);
        }
    }
}
