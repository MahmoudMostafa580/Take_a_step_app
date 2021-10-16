package com.example.takeastep.models;

public class ReadyContent {
    String videoUrl,caption,category;
    Long time;
    public ReadyContent() {
    }

    public ReadyContent(String videoUrl, String caption, String category,Long time) {
        this.videoUrl=videoUrl;
        this.caption = caption;
        this.category = category;
        this.time=time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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
