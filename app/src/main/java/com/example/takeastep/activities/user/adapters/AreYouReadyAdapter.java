package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeastep.R;
import com.example.takeastep.models.ReadyContent;

import java.util.ArrayList;

public class AreYouReadyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    private Context mContext;

    public static final int VIEW_TYPE_IMAGE = 1;
    public static final int VIEW_TYPE_VIDEO = 2;

    public AreYouReadyAdapter(ArrayList<ReadyContent> contentsList, Context mContext) {
        this.contentsList = contentsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_IMAGE) {
            return new AreYouReadyImageViewHolder(LayoutInflater.from(mContext).
                    inflate(R.layout.user_item_areyouready_image_container, parent, false));
        } else {
            return new AreYouReadyVideoViewHolder(LayoutInflater.from(mContext).
                    inflate(R.layout.user_item_areyouready_video_container, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ReadyContent currentContent = contentsList.get(position);
            ((AreYouReadyImageViewHolder) holder).contentImage.setImageURI(Uri.parse(currentContent.getImageUrl()));
            Glide.with(mContext).load(currentContent.getImageUrl()).into(((AreYouReadyImageViewHolder) holder).contentImage);
            ((AreYouReadyImageViewHolder) holder).contentCaption.setText(currentContent.getCaption());
        } else {
            ReadyContent currentContent = contentsList.get(position);
            MediaController controller=new MediaController(mContext);
            ((AreYouReadyVideoViewHolder) holder).contentVideo.setMediaController(controller);
            ((AreYouReadyVideoViewHolder) holder).contentVideo.setVideoURI(Uri.parse(currentContent.getVideoUrl()));
            //((AreYouReadyVideoViewHolder) holder).contentVideo.requestFocus();
            ((AreYouReadyVideoViewHolder) holder).contentVideo.seekTo(50);
            ((AreYouReadyVideoViewHolder) holder).contentCaption.setText(currentContent.getCaption());
        }

    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (contentsList.get(position).getVideoUrl() == null)
            return VIEW_TYPE_IMAGE;
        else
            return VIEW_TYPE_VIDEO;
    }


    public static class AreYouReadyImageViewHolder extends RecyclerView.ViewHolder {

        ImageView contentImage;
        TextView contentCaption;

        public AreYouReadyImageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.content_image);
            contentCaption = itemView.findViewById(R.id.content_caption);
        }
    }

    public static class AreYouReadyVideoViewHolder extends RecyclerView.ViewHolder {

        VideoView contentVideo;
        TextView contentCaption;

        public AreYouReadyVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            contentVideo = itemView.findViewById(R.id.content_video);
            contentCaption = itemView.findViewById(R.id.content_caption);
        }
    }


}
