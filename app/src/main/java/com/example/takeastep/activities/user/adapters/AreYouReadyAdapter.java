package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        holder.contentCaption.setText(currentContent.getCaption());

        LoadControl loadControl = new DefaultLoadControl();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(mContext, new AdaptiveTrackSelection.Factory());
        CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory().setUpstreamDataSourceFactory(new DefaultHttpDataSourceFactory());

        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(cacheFactory);
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(mContext)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build();
        player.addMediaItem(position, MediaItem.fromUri(currentContent.getVideoUrl()));
        player.prepare();
        player.setPlayWhenReady(true);
        player.play();
        holder.playerView.setPlayer(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    holder.progressBar.setVisibility(View.GONE);
                } else {
                    holder.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }


    public static class AreYouReadyVideoViewHolder extends RecyclerView.ViewHolder {

        TextView contentCaption;
        PlayerView playerView;
        ProgressBar progressBar;

        public AreYouReadyVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            contentCaption = itemView.findViewById(R.id.content_caption);
            playerView = itemView.findViewById(R.id.content_video);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
        }
    }


}
