package com.example.takeastep.activities.admin.adapters;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.ReadyContent;

import com.google.android.exoplayer2.MediaItem;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import com.google.android.exoplayer2.ui.PlayerView;



import java.util.ArrayList;
import java.util.Collections;

public class ReadyContentAdapter extends RecyclerView.Adapter<ReadyContentAdapter.ReadyContentVideoViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    public Context mContext;
    private OnItemClickListener mListener;

    public ReadyContentAdapter(ArrayList<ReadyContent> contentsList, Context mContext) {
        this.contentsList = contentsList;
        this.mContext = mContext;
    }

    public interface OnItemClickListener {
        void onMenuClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ReadyContentVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReadyContentVideoViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_areyouready_video_container
                        , parent
                        , false)
                , mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadyContentVideoViewHolder holder, int position) {
        ReadyContent currentContent = contentsList.get(position);
        holder.setExoplayer(mContext,currentContent.getCaption(), currentContent.getVideoUrl());

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ReadyContentVideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }


    public static class ReadyContentVideoViewHolder extends RecyclerView.ViewHolder {

        ImageView contentMenu;
        ProgressBar progressBar;

        public ReadyContentVideoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            contentMenu = itemView.findViewById(R.id.content_menu);

            progressBar = itemView.findViewById(R.id.videoProgressBar);

            contentMenu.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMenuClick(position, v);
                    }
                }
            });
        }

        public void setExoplayer(Context context,String caption, String videoUrl){
            TextView contentCaption=itemView.findViewById(R.id.content_caption);
            PlayerView playerView = itemView.findViewById(R.id.content_video);

            contentCaption.setText(caption);

            try{
                SimpleExoPlayer simpleExoPlayer=new SimpleExoPlayer.Builder(context).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaitem=MediaItem.fromUri(videoUrl);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaitem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState==Player.STATE_BUFFERING){
                            progressBar.setVisibility(View.VISIBLE);
                        }else if (playbackState==Player.STATE_READY){
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }catch (Exception e){
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
