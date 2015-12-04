package com.jkpg.jurgen.nl.vacationdroid.datamodels;

public class Vacation {
    private int id;
    private String title;
    private String description;
    private String place;
    private int start;
    private int end;
    private int userId;

    public Vacation(int id, String title, String description, String place, int start, int end, int userId){
        this.id = id;
        this.title = title;
        this.description = description;
        this.place = place;
        this.start = start;
        this.end = end;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString(){
        return "[Vacation] id:"+id+"\ttitle:"+title+"\tdescription:"+description+"place:"+place+"\tstart:"+start+"\tend:"+end+"\tuserId:"+userId;
    }

}

