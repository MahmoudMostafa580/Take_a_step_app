package com.example.takeastep.models;

public class Vaccine {
    String name,info;

    public Vaccine(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public Vaccine() {
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
