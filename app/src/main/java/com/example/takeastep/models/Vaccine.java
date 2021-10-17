package com.example.takeastep.models;

public class Vaccine {
    String name,info,image;

    public Vaccine(String name, String info,String image) {
        this.name = name;
        this.info = info;
        this.image=image;
    }

    public Vaccine() {
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
