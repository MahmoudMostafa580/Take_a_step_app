package com.example.takeastep.models;

public class Vaccine {
    String name,info,image;
    Long time;

    public Vaccine(String name, String info,String image,Long time) {
        this.name = name;
        this.info = info;
        this.image=image;
        this.time=time;
    }

    public Vaccine() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
