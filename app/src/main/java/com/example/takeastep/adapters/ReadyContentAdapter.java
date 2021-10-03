package com.example.takeastep.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeastep.R;
import com.example.takeastep.models.ReadyContent;

import java.util.ArrayList;

public class ReadyContentAdapter extends RecyclerView.Adapter<ReadyContentAdapter.ReadyContentViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    private Context mContext;
    private OnItemClickListener mListener;

    public ReadyContentAdapter(ArrayList<ReadyContent> contentsList, Context mContext) {
        this.contentsList = contentsList;
        this.mContext = mContext;
    }

    public interface OnItemClickListener{
        void onMenuClick(int position,View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public ReadyContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReadyContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_areyouready_container, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadyContentViewHolder holder, int position) {
        ReadyContent currentContent=contentsList.get(position);
        holder.contentImage.setImageURI(Uri.parse(currentContent.getUrl()));
        Glide.with(mContext).load(currentContent.getUrl()).into(holder.contentImage);
        holder.contentVideo.setVideoURI(Uri.parse(currentContent.getUrl()));
        holder.contentVideo.start();
        holder.contentCaption.setText(currentContent.getCaption());

    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    public static class ReadyContentViewHolder extends RecyclerView.ViewHolder {

        ImageView contentImage,contentMenu;
        VideoView contentVideo;
        TextView contentCaption;

        public ReadyContentViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            contentImage=itemView.findViewById(R.id.content_image);
            contentVideo=itemView.findViewById(R.id.content_video);
            contentCaption=itemView.findViewById(R.id.content_caption);
            contentMenu=itemView.findViewById(R.id.content_menu);

            contentMenu.setOnClickListener(v -> {
                if (listener!=null){
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        listener.onMenuClick(position,v);
                    }
                }
            });
        }
    }
}
