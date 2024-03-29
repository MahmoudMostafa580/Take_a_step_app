package com.example.takeastep.activities.user.adapters;

import static com.example.takeastep.activities.LauncherActivity.exoPlayersVideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.activities.LauncherActivity;
import com.example.takeastep.models.ReadyContent;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.Collections;

public class AreYouReadyAdapter extends RecyclerView.Adapter<AreYouReadyAdapter.AreYouReadyVideoViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    private Context mContext;

    public static PlayerView playerView;
    public static SimpleExoPlayer simpleExoPlayer;


    public AreYouReadyAdapter(ArrayList<ReadyContent> contentsList, Context mContext) {
        this.contentsList = contentsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AreYouReadyVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new AreYouReadyVideoViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.user_item_areyouready_video_container, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AreYouReadyVideoViewHolder holder, int position) {

        ReadyContent currentContent = contentsList.get(position);
        holder.contentCaption.setText(currentContent.getCaption());
        try {
            exoPlayersVideo = new SimpleExoPlayer.Builder(mContext).build();
            holder.playerView.setPlayer(exoPlayersVideo);
            MediaItem mediaitem = MediaItem.fromUri(currentContent.getVideoUrl());
            exoPlayersVideo.addMediaItems(Collections.singletonList(mediaitem));
            exoPlayersVideo.prepare();
            exoPlayersVideo.setPlayWhenReady(false);
            exoPlayersVideo.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    } else if (playbackState == Player.STATE_READY) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }
            });

            LauncherActivity.mapExoPlayersvideo.put(position, exoPlayersVideo);

            holder.playImg.setOnClickListener(v -> {
                LauncherActivity.stopVideos(position);
                if (LauncherActivity.mapExoPlayersvideo.get(position).getCurrentPosition() >= LauncherActivity.mapExoPlayersvideo.get(position).getDuration()) {
                    LauncherActivity.mapExoPlayersvideo.get(position).seekTo(1);
                }
                LauncherActivity.mapExoPlayersvideo.get(position).play();
            });
        } catch (Exception e) {
            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AreYouReadyVideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    public static class AreYouReadyVideoViewHolder extends RecyclerView.ViewHolder {


        ImageView playImg;
        ProgressBar progressBar;
        TextView contentCaption;
        PlayerView playerView;

        public AreYouReadyVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
            playImg = itemView.findViewById(R.id.exo_play);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
            contentCaption = itemView.findViewById(R.id.content_caption);
            playerView = itemView.findViewById(R.id.content_video);
        }
    }

}
