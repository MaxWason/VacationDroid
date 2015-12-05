package com.jkpg.jurgen.nl.vacationdroid.datamodels;

public class Vacation {
    public int id;
    public String title;
    public String description;
    public String place;
    public int start;
    public int end;
    public String user;

    public Vacation(int id, String tit, String desc, String place, int start, int end, String usern) {
        this.id = id;
        title = tit;
        description = desc;
        this.place = place;
        this.start = start;
        this.end = end;
        this.user = usern;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPlace() {
        return place;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

//    public int getUserId() {
//        return userId;
//    }

    @Override
    public String toString() {
        return "[Vacation] id:" + id + "\ttitle:" + title + "\tdescription:" + description + "place:" + place + "\tstart:" + start + "\tend:" + end;// + "\tuserId:" + userId;
    }


}

