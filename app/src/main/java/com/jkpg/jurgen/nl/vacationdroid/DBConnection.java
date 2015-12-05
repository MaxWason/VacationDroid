package com.jkpg.jurgen.nl.vacationdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.User;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class DBConnection extends SQLiteOpenHelper {

    public static String dbname = "vacationdb";
    public DBConnection(Context context) {
        super(context, dbname, null, 1);

    }

    public void addOrUpdateVacation(Vacation v) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", v.id);
        cv.put("title", v.title);
        cv.put("description", v.description);
        cv.put("place", v.place);
        cv.put("start", 1);
        cv.put("end", 2);
        cv.put("usern",v.user);

        db.insertWithOnConflict("vacations", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void addOrUpdateMemory(Memory m) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", m.id);
        cv.put("title", m.title);
        cv.put("description", m.description);
        cv.put("place", m.place);
        cv.put("time", m.time);

        db.insertWithOnConflict("memories", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void addOrUpdateUser(User f) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", f.id);
        cv.put("username", f.username);


        db.insertWithOnConflict("users", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public ArrayList<Vacation> getVacations() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Vacation> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM vacations", new String[]{});
        while(c.moveToNext()) {
            output.add(new Vacation(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4),
                    c.getInt(5),
                    c.getString(6)
                    ));
        }

        return output;
    }

    public ArrayList<User> getUsers() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<User> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM users", new String[]{});
        while(c.moveToNext()) {
            output.add(new User(
                    c.getInt(0),
                    c.getString(1)
            ));
        }

        return output;
    }

    public ArrayList<Vacation> getUserVacations(String uname) {

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Vacation> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM vacations WHERE usern = '" + uname + "'", new String[]{});

        while(c.moveToNext()) {
            output.add(new Vacation(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4),
                    c.getInt(5),
                    c.getString(6)
            ));
        }

        return output;
    }

    public ArrayList<Memory> getMemoriesByVacation(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Memory> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM memories WHERE _id = "+ id, new String[]{});
        while(c.moveToNext()) {
            output.add(new Memory(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4),
                    c.getInt(5)
            ));
        }

        return output;
    }


    public Vacation getVacationById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM vacations WHERE _id = " + id, new String[]{});
        c.moveToNext();

        Vacation v = new Vacation(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4),
                    c.getInt(5),
                    c.getString(6)
        );

        return v;
    }

    public void clearDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM vacations");
        db.execSQL("DELETE FROM memories");
        db.execSQL("DELETE FROM vacations");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE vacations(" +
                "_id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "place TEXT," +
                "start INTEGER," +
                "end INTEGER," +
                "usern INTEGER);");

        db.execSQL("CREATE TABLE memories(" +
                "_id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "place TEXT," +
                "time INT);");

        db.execSQL("CREATE TABLE users(" +
                "_id INTEGER PRIMARY KEY," +
                "username TEXT NOT NULL);");

        db.execSQL("CREATE TABLE medias(" +
                "_id INTEGER PRIMARY KEY," +
                "memoryid INTEGER," +
                "url TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
