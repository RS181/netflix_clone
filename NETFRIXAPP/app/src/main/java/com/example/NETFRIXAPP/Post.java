package com.example.NETFRIXAPP;

import com.google.gson.annotations.SerializedName;


//{"movieid":8,
// "moviename":"Bunny rabit",
// "duration":"60m",
// "year":"2023",
// "link_low":"https://35.195.14.2/video/testfile_360p.json/master.m3u8",
// "link_high":"https://35.195.14.2/video/testfile_1080p.json/master.m3u8"}


public class Post {
    // Login validation
    private String login;
    private String password;

    //Login Post response
    private String status;

    //About the movie

    private String movieid;
    private String moviename;
    private String duration;
    private String year;
    private String link_low;
    private String link_high;

    private String title;

    @SerializedName("body")
    private String text;

    public Post(String user, String pass){
        this.login = user;
        this.password = pass;
    }

    public Post(String Id){
        this.movieid = Id;
        this.login = Id;
    }

    public String getfake() {
        return title;
    }

    public String getPassword() {
            return password;
        }

    public String getLogin() {
            return login;
        }

    public String getStatus() { return status; }

    //ABOUT the movie
    public String getMovieID() { return movieid; }

    public String getMovieName() { return moviename; }

    public String getMovieDuration() { return duration; }

    public String getMovieYear() { return year; }

    public String getMovieLow() { return link_low; }

    public String getMoviehigh() { return link_high; }

}
