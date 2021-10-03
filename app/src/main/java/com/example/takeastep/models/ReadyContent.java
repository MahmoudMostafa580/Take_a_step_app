package com.example.takeastep.models;

public class ReadyContent {
    String url,caption,category,time;

    public ReadyContent() {
    }

    public ReadyContent(String url, String caption, String category,String time) {
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
