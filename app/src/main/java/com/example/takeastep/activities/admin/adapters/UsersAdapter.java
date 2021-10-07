package com.example.takeastep.activities.admin.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.takeastep.R;
import com.example.takeastep.models.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private ArrayList<User> usersList = new ArrayList<>();
    Context mContext;
    private OnItemClickListener mListener;


    public UsersAdapter(Context mContext, ArrayList<User> usersList){
        this.mContext=mContext;
        this.usersList=usersList;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Glide.with(mContext)
                .load(Uri.parse(usersList.get(position).getImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(holder.imageProfile);
        holder.nameTxt.setText(usersList.get(position).getName());
        holder.emailTxt.setText(usersList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProfile;
        TextView nameTxt,emailTxt;

        public UsersViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            nameTxt=itemView.findViewById(R.id.name_txt);
            emailTxt=itemView.findViewById(R.id.email_txt);

            itemView.setOnClickListener(v -> {
                if (listener!=null){
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
