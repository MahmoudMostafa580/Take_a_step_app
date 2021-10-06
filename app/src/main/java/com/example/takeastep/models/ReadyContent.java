package com.example.takeastep.models;

public class ReadyContent {
    String imageUrl,videoUrl,caption,category,time;

    public ReadyContent() {
    }

    public ReadyContent(String imageUrl,String videoUrl, String caption, String category,String time) {
        this.imageUrl = imageUrl;
        this.videoUrl=videoUrl;
        this.caption = caption;
        this.category = category;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
