package com.example.takeastep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.Country;

import java.util.ArrayList;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private ArrayList<Country> countriesList;
    private Context mContext;
    private OnItemClickListener mListener;

    public CountryAdapter(ArrayList<Country> countriesList, Context mContext) {
        this.countriesList = countriesList;
        this.mContext = mContext;
    }

    public interface OnItemClickListener{
        void onMenuClick(View view,int position);
    }

    public void setOnItemClickListener(CountryAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CountryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_country_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country currentCountry=countriesList.get(position);
        holder.countryName.setText(currentCountry.getName());
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {

        TextView countryName;
        ImageView menu;
        public CountryViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            countryName=itemView.findViewById(R.id.country_name);
            menu=itemView.findViewById(R.id.delete_img);

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
