package com.example.takeastep.activities.admin.adapters;

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

public class ReadyContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ReadyContent> contentsList;
    private Context mContext;
    private OnItemClickListener mListener;
    public static final int VIEW_TYPE_IMAGE = 1;
    public static final int VIEW_TYPE_VIDEO = 2;

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_IMAGE) {
            return new ReadyContentImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_areyouready_image_container, parent, false), mListener);
        } else {
            return new ReadyContentVideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_areyouready_video_container, parent, false), mListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ReadyContent currentContent = contentsList.get(position);
            ((ReadyContentImageViewHolder) holder).contentImage.setImageURI(Uri.parse(currentContent.getImageUrl()));
            Glide.with(mContext).load(currentContent.getImageUrl()).into(((ReadyContentImageViewHolder) holder).contentImage);
            ((ReadyContentImageViewHolder) holder).contentCaption.setText(currentContent.getCaption());

        } else {
            ReadyContent currentContent = contentsList.get(position);
            MediaController controller=new MediaController(mContext);
            ((ReadyContentVideoViewHolder) holder).contentVideo.setVideoURI(Uri.parse(currentContent.getVideoUrl()));
            ((ReadyContentVideoViewHolder) holder).contentVideo.setMediaController(controller);
            ((ReadyContentVideoViewHolder) holder).contentVideo.seekTo(50);
            ((ReadyContentVideoViewHolder) holder).contentVideo.requestFocus();
            ((ReadyContentVideoViewHolder) holder).contentCaption.setText(currentContent.getCaption());
        }
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (contentsList.get(position).getVideoUrl()==null)
            return VIEW_TYPE_IMAGE;
        else
            return VIEW_TYPE_VIDEO;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public static class ReadyContentImageViewHolder extends RecyclerView.ViewHolder {

        ImageView contentImage, contentMenu;
        TextView contentCaption;

        public ReadyContentImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.content_image);
            contentCaption = itemView.findViewById(R.id.content_caption);
            contentMenu = itemView.findViewById(R.id.content_menu);

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

    public static class ReadyContentVideoViewHolder extends RecyclerView.ViewHolder {
        VideoView contentVideo;
        TextView contentCaption;
        ImageView contentMenu;

        public ReadyContentVideoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            contentVideo = itemView.findViewById(R.id.content_video);
            contentCaption = itemView.findViewById(R.id.content_caption);
            contentMenu = itemView.findViewById(R.id.content_menu);

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
