package com.jkpg.jurgen.nl.vacationdroid.datamodels;

/**
 * Created by Jurgen on 11/5/2015.
 */
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

}
