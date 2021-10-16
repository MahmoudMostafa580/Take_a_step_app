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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private ArrayList<User> usersList;
    Context mContext;
    private OnItemClickListener mListener;

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

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
        return new UsersViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd . hh:mm a", Locale.getDefault());

        calendar.setTimeInMillis(usersList.get(position).getLastMessageTime());
        String time=sdf.format(calendar.getTime());

        Glide.with(mContext)
                .load(usersList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(holder.imageProfile);
        holder.nameTxt.setText(usersList.get(position).getName());
        holder.lastMessageTxt.setText(usersList.get(position).getLastMessage());
        holder.lastMessageTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProfile;
        TextView nameTxt,lastMessageTxt,lastMessageTime;

        public UsersViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            nameTxt=itemView.findViewById(R.id.name_txt);
            lastMessageTxt=itemView.findViewById(R.id.last_message_txt);
            lastMessageTime=itemView.findViewById(R.id.last_message_time);

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
