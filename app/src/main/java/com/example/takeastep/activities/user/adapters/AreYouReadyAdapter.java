package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.ReadyContent;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;

import java.util.ArrayList;
import java.util.Collections;

public class AreYouReadyAdapter extends RecyclerView.Adapter<AreYouReadyAdapter.AreYouReadyVideoViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    private Context mContext;


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
        holder.setExoplayer(mContext,currentContent.getCaption(), currentContent.getVideoUrl());

    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }


    public static class AreYouReadyVideoViewHolder extends RecyclerView.ViewHolder {


        ProgressBar progressBar;

        public AreYouReadyVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
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
