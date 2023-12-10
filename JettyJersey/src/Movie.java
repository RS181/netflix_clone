// package com.mkyong;

public class Movie {

    private int movieid;

    private String moviename;

    private String duration;

    private String year;

    private String link_low;
    private String link_high;


    public int getmovieid() {
        return movieid;
    }

    public void setmovieid(int movieid) {
        this.movieid = movieid;
    }

    public String getmoviename() {
        return moviename;
    }

    public void setmoviename(String moviename) {
        this.moviename = moviename;
    }

    public String getduration() {
        return duration;
    }

    public void setduration(String duration) {
        this.duration = duration;
    }

    public String getyear() {
        return year;
    }

    public void setyear(String year) {
        this.year = year;
    }

    public String getlink_low() {
        return link_low;
    }

    public void setlink_low(String link) {
        link_low = link;
    }

    public String getlink_high() {
        return link_high;
    }

    public void setlink_high(String link) {
        link_high = link;
    }

}
