package com.jkpg.jurgen.nl.vacationdroid.datamodels;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class Vacation {
    public String title;
    public String description;
    public String place;
    public int start;
    public int end;

    public Vacation(String tit, String desc, String place, int start, int end) {
        title = tit;
        description = desc;
        this.place = place;
        this.start = start;
        this.end = end;
    }

}
