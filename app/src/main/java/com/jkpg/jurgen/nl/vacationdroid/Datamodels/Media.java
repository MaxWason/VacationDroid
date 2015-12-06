package com.jkpg.jurgen.nl.vacationdroid.datamodels;

/**
 * Created by Jurgen on 12/6/2015.
 */
public class Media {
    public int id;
    public int memoryid;
    public String fileurl;

    public Media(int ide, int memid, String file) {
        id = ide;
        memoryid = memid;
        fileurl = file;
    }
}
