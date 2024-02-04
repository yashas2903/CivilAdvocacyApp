package com.example.civiladvocacy;

import java.io.Serializable;

public class Officials implements Serializable {


    private final String name,role,address,party,phone,urls,email,photo,facebook,twitter,youtube;


    public String getName() {
        return name;
    }

    public Officials(String name, String role,String address, String party, String phone, String urls, String email, String photo, String facebook, String twitter, String youtube){
        this.name = name;
        this.role = role;
        this.address = address;
        this.party = party;
        this.phone = phone;
        this.urls = urls;
        this.email = email;
        this.photo = photo;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;

    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrls() {
        return urls;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }
}
