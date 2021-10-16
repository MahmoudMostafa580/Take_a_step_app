package com.example.takeastep.activities.admin.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.ReadyContent;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;

import java.util.ArrayList;

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
        holder.contentCaption.setText(currentContent.getCaption());

        LoadControl loadControl=new DefaultLoadControl();
        DefaultTrackSelector trackSelector=new DefaultTrackSelector(mContext,new AdaptiveTrackSelection.Factory());
        CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory().setUpstreamDataSourceFactory(new DefaultHttpDataSourceFactory());

        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(cacheFactory);
        SimpleExoPlayer player=new SimpleExoPlayer.Builder(mContext)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build();
        player.addMediaItem(position,MediaItem.fromUri(currentContent.getVideoUrl()));
        player.prepare();
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
                if (isPlaying){
                    holder.progressBar.setVisibility(View.GONE);
                }else{
                    holder.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ReadyContentVideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.playerView.getPlayer().play();
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }


    public static class ReadyContentVideoViewHolder extends RecyclerView.ViewHolder {
        TextView contentCaption;
        ImageView contentMenu;

        PlayerView playerView;
        ProgressBar progressBar;

        public ReadyContentVideoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            contentCaption = itemView.findViewById(R.id.content_caption);
            contentMenu = itemView.findViewById(R.id.content_menu);
            playerView = itemView.findViewById(R.id.content_video);
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

    }
}
