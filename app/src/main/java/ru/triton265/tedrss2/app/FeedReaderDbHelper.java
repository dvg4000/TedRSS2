package ru.triton265.tedrss2.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FeedReaderDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            " CREATE TABLE " + FeedReaderContract.FeedItem.TABLE_NAME + " ( " +
                    FeedReaderContract.FeedItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_CATEGORY + " TEXT, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_LINK + " TEXT, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_PUB_DATE + " INTEGER, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_THUMBNAIL_LINK + " TEXT, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_TITLE + " TEXT, " +
                    FeedReaderContract.FeedItem.COLUMN_NAME_VIDEO_LINK + " TEXT " +
            " ); ";

    private static final String SQL_DELETE_ENTRIES =
            " DROP TABLE IF EXISTS " + FeedReaderContract.FeedItem.TABLE_NAME + " ;";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
