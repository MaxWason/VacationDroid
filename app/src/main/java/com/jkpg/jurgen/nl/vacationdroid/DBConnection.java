package com.jkpg.jurgen.nl.vacationdroid;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jkpg.jurgen.nl.vacationdroid.datamodels.Media;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.User;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class DBConnection extends SQLiteOpenHelper {

    public static String dbname = "vacationdb";
    private Context context;

    public DBConnection(Context context) {
        super(context, dbname, null, 1);
        this.context = context;
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
        db.close();
    }

    public void addOrUpdateMemory(Memory m) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", m.id);
        cv.put("title", m.title);
        cv.put("description", m.description);
        cv.put("place", m.place);
        cv.put("time", m.time);
        cv.put("vacationid", m.vacationid);

        db.insertWithOnConflict("memories", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addOrUpdateUser(User f) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", f.id);
        cv.put("username", f.username);


        db.insertWithOnConflict("users", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addOrUpdateMedia(Media m) {

        reCreateMedias(); // FIXME: 12/15/2015

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("ALTER TABLE " + "medias" + " ADD COLUMN " + "type"+ " TEXT"); // FIXME: 12/15/2015

        ContentValues cv = new ContentValues();
        cv.put("_id", m.id);
        cv.put("memoryid", m.memoryid);
        cv.put("url", m.fileurl);
        cv.put("type", m.type);


        db.insertWithOnConflict("medias", "_id = ?", cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // FIXME: 12/15/2015
    private void reCreateMedias() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + "medias");
        String sql = "CREATE TABLE " + "medias" + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "memoryid INTEGER, "
                + "url TEXT NOT NULL, "
                + "type" + "TEXT NOT NULL" + ");";
        db.execSQL(sql);
        db.close();
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

        db.close();
        return output;
    }

    public ArrayList<User> getFriends() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<User> output = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        String uname = sp.getString("username", "error");
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username IS NOT '" + uname + "'", new String[]{});
        while(c.moveToNext()) {
            output.add(new User(
                    c.getInt(0),
                    c.getString(1)
            ));
        }
        db.close();
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
        db.close();
        return output;
    }

    public ArrayList<Media> getMediasByMemory(int memid) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Media> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM medias WHERE memoryid =" + memid, new String[]{});
        while(c.moveToNext()) {
            output.add(new Media(
                    c.getInt(0),
                    c.getInt(1),
                    c.getString(2),
                    c.getString(3)
            ));
        }
        db.close();
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
        db.close();
        return output;
    }

    public ArrayList<Memory> getMemoriesByVacation(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Memory> output = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM memories WHERE vacationid = "+ id, new String[]{});
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
        db.close();
        return output;
    }

    public void deleteFromTable(int id, String table) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(table, "_id = "+ id, null);
    }
    public Vacation getVacationById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM vacations WHERE _id = " + id, new String[]{});

        if (c.getCount() == 0) {
            return null;
        }
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
        db.close();
        return v;
    }

    public Memory getMemoryById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM memories WHERE _id = " + id, new String[]{});

        if (c.getCount() == 0) {
            return null;
        }
        c.moveToNext();
        Memory m = new Memory(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4),
                    c.getInt(5)
            );

        db.close();
        return m;
    }

    public int getUserByName(String name) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT _id FROM users WHERE username = '" + name + "'", new String[]{});

        if (c.getCount() == 0) {
            return -1;
        }
        c.moveToNext();
        int id = c.getInt(0);

        db.close();
        return id;
    }


    public void clearDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM vacations");
        db.execSQL("DELETE FROM memories");
        db.execSQL("DELETE FROM users");
        db.execSQL("DELETE FROM medias");
        db.close();

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
                "time INT," +
                "vacationid INT);");

        db.execSQL("CREATE TABLE users(" +
                "_id INTEGER PRIMARY KEY," +
                "username TEXT NOT NULL);");

        db.execSQL("CREATE TABLE medias ("
                + "_id INTEGER PRIMARY KEY,"
                + "memoryid INTEGER,"
                + "url TEXT,"
                + "type TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
