package com.example.takeastep.activities.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.User;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {
    private ArrayList<User> requestsList;
    private Context mContext;
    private OnItemClickListener mListener;

    public RequestsAdapter(ArrayList<User> usersList, Context mContext) {
        this.requestsList = usersList;
        this.mContext = mContext;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_request_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
        User currentUser=requestsList.get(position);
        holder.userName.setText(currentUser.getName());
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName;

        public RequestsViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            userName=itemView.findViewById(R.id.name_text);

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
