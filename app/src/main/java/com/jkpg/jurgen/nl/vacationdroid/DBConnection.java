package com.jkpg.jurgen.nl.vacationdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class DBConnection extends SQLiteOpenHelper {

    public static String dbname = "vacationdb";

    public DBConnection(Context context) {
        super(context, dbname, null, 1);

    }

    public void addVacation(Vacation v) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("vacationdb", null, SQLiteDatabase.OPEN_READWRITE);

        String values = v.title + "," +
                v.description + "," +
                v.place + "," +
                v.start + "," +
                v.end;
        db.execSQL("INSERT INTO vacations" +
                "values (" + values + ");");


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE vacations(" +
                "_id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "start INTEGER," +
                "end INTEGER");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
