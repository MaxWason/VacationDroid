package com.jkpg.jurgen.nl.vacationdroid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class VacationDroidContentProvider extends ContentProvider {

    //uri for media
    static final String PROVIDER_NAME = "com.jkpg.jurgen.nl.vacationdroid";
    static final String URL = "content://" + PROVIDER_NAME + "/media";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final int MEDIA = 1;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "media", MEDIA);
    }

    //databaseHelper and db itself
    private DBConnection dbHelper;
    private SQLiteDatabase db;

    /**
     * When the application starts get the database to manipulate.
     * @return true if it successfully establishes the database
     */
    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new DBConnection(context);
        db = dbHelper.getWritableDatabase();

        return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Insert to add a new media with the values passed in as a parameter.
     * @param uri - the uri to manipulate
     * @param values - the values to use in the new media. They should be: _id, memoryid, url, type
     * @return - the uri if successful
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //update database
        long row = db.insertWithOnConflict("medias", "_id = ?", values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        //get uri and update if possible, otherwise throw an error
        if (row > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
