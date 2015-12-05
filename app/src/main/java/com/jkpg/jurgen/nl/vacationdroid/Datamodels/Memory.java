package com.jkpg.jurgen.nl.vacationdroid.datamodels;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class Memory {
    public int id;
    public String title;
    public String description;
    public String place;
    public int time;
    public int vacationid;


    public Memory(int id, String title, String description, String place, int time, int vacationid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.place = place;
        this.time = time;
        this.vacationid = vacationid;
    }

}
