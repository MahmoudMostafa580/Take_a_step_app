package com.example.takeastep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.Vaccine;

import java.util.ArrayList;

public class AdminVaccineTypesAdapter extends RecyclerView.Adapter<AdminVaccineTypesAdapter.VaccineTypesViewHolder> {
    private ArrayList<Vaccine> mVaccinesList;
    private Context mContext;
    private OnItemClickListener mListener;

    public AdminVaccineTypesAdapter(Context context, ArrayList<Vaccine> vaccinesList) {
        mContext = context;
        mVaccinesList = vaccinesList;
    }

    public interface OnItemClickListener {
        void onMenuClick(View view,int position);
    }

    public void setOnItemClickListener(AdminVaccineTypesAdapter.OnItemClickListener listener){
        mListener=listener;
    }
    @NonNull
    @Override
    public VaccineTypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VaccineTypesViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_vaccine_type_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineTypesViewHolder holder, int position) {
        Vaccine currentVaccine = mVaccinesList.get(position);
        holder.vaccineName.setText(currentVaccine.getName());
    }

    @Override
    public int getItemCount() {
        return mVaccinesList.size();
    }

    public static class VaccineTypesViewHolder extends RecyclerView.ViewHolder {
        TextView vaccineName;
        ImageView menu;

        public VaccineTypesViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            vaccineName = itemView.findViewById(R.id.vaccine_name);
            menu = itemView.findViewById(R.id.menu_image);

            menu.setOnClickListener(v -> {
                if (listener!=null){
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        listener.onMenuClick(v,position);
                    }
                }
            });
        }
    }
}
