package com.jkpg.jurgen.nl.vacationdroid.datamodels;

/**
 * Created by Jurgen on 12/6/2015.
 */
public class Media {
    public int id;
    public int memoryid;
    public String fileurl;
    public String type;

    public Media(int ide, int memid, String file, String type) {
        id = ide;
        memoryid = memid;
        fileurl = file;
        this.type = type;
    }
}
