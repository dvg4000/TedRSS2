package ru.triton265.tedrss2.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TedContentProvider extends ContentProvider {
    private static final int ITEM_LIST = 1;
    private static final int ITEM_ID = 2;
    private static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(TedContentContract.AUTHORITY, TedContentContract.FeedItem.ITEMS, ITEM_LIST);
        URI_MATCHER.addURI(TedContentContract.AUTHORITY, TedContentContract.FeedItem.ITEMS + "/#", ITEM_ID);
    }

    private FeedReaderDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new FeedReaderDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return TedContentContract.FeedItem.CONTENT_ITEM_LIST_TYPE;
            case ITEM_ID:
                return TedContentContract.FeedItem.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int delCount;

        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                delCount = db.delete(FeedReaderContract.FeedItem.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                String where = FeedReaderContract.FeedItem._ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) { where += " AND " + selection; }
                delCount = db.delete(FeedReaderContract.FeedItem.TABLE_NAME, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (delCount > 0) { getContext().getContentResolver().notifyChange(uri, null); }
        return delCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != ITEM_LIST) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final long id = db.insert(FeedReaderContract.FeedItem.TABLE_NAME, null, values);
        if (id > 0) {
            final Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                if (TextUtils.isEmpty(sortOrder)) { sortOrder = TedContentContract.FeedItem.SORT_ORDER_DEFAULT; }
                builder.setTables(FeedReaderContract.FeedItem.TABLE_NAME);
                break;
            case ITEM_ID:
                builder.setTables(FeedReaderContract.FeedItem.TABLE_NAME);
                builder.appendWhere(FeedReaderContract.FeedItem._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        final Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updateCount;

        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                updateCount = db.update(FeedReaderContract.FeedItem.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_ID:
                String where = FeedReaderContract.FeedItem._ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) { where += " AND " + selection; }
                updateCount = db.update(FeedReaderContract.FeedItem.TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (updateCount > 0) { getContext().getContentResolver().notifyChange(uri, null); }
        return updateCount;
    }
}
