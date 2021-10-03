package com.example.takeastep.models;

import java.io.Serializable;

public class User implements Serializable {
    public String name,image,email,id;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String image, String email, String id) {
        this.name = name;
        this.image = image;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
