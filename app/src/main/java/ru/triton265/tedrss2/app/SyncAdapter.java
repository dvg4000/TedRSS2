package ru.triton265.tedrss2.app;

import android.accounts.Account;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncAdapter extends AbstractThreadedSyncAdapter{
    private static final String DEBUG_TAG = SyncAdapter.class.getSimpleName();
    static final String FEED_URL = "http://www.ted.com/talks/rss";
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private static final String[] PROJECTION = new String[]{
            TedContentContract.FeedItem._ID,
            TedContentContract.FeedItem.LINK
    };

    private final int COLUMN_LINK = 1;
    private final int COLUMN_ID = 0;

    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = getContext().getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = getContext().getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult)
    {
        try {
            final URL location = new URL(FEED_URL);
            InputStream stream = null;
            try {
                stream = downloadUrl(location);
                updateLocalFeedData(stream, syncResult);
            } finally {
                if (null != stream) { stream.close(); }
            }
        } catch (MalformedURLException e) {
            Log.wtf(DEBUG_TAG, "MalformedURLException", e);
            syncResult.stats.numParseExceptions++;
        } catch (IOException e) {
            Log.wtf(DEBUG_TAG, "IOException", e);
            syncResult.stats.numIoExceptions++;
        } catch (XmlPullParserException e) {
            Log.wtf(DEBUG_TAG, "XmlPullParserException", e);
            syncResult.stats.numParseExceptions++;
        } catch (ParseException e) {
            Log.wtf(DEBUG_TAG, "ParseException", e);
            syncResult.stats.numParseExceptions++;
        } catch (OperationApplicationException e) {
            Log.wtf(DEBUG_TAG, "OperationApplicationException", e);
            syncResult.databaseError = true;
        } catch (RemoteException e) {
            Log.wtf(DEBUG_TAG, "RemoteException", e);
            syncResult.databaseError = true;
        }
    }

    private void updateLocalFeedData(final InputStream stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, ParseException, RemoteException, OperationApplicationException
    {
        final ContentResolver contentResolver = getContext().getContentResolver();
        final List<FeedParser.FeedItem> entries = new FeedParser().parse(stream);

        // Build hash table of incoming entries
        final Map<String, FeedParser.FeedItem> itemMap = new HashMap<>();
        for (FeedParser.FeedItem item : entries) { itemMap.put(item.mLink, item); }

        final ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        final Uri uri = TedContentContract.FeedItem.CONTENT_URI;
        final Cursor cursor = contentResolver.query(uri, PROJECTION, null, null, null);
        assert cursor != null;
        //if (null == cursor) { throw  new OperationApplicationException("cursor is null");

        // Find stale data
        while (cursor.moveToNext()) {
            syncResult.stats.numEntries++;

            final int id = cursor.getInt(COLUMN_ID);
            final String link = cursor.getString(COLUMN_LINK);

            FeedParser.FeedItem item = itemMap.get(link);
            if (null != item) {
                // Entry exists. Remove from entry map to prevent insert later. 
                itemMap.remove(link);
                // TODO: update.
            } else {
                // Entry doesn't exist. Remove it from the database.
                final Uri deleteUri = TedContentContract.FeedItem.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        cursor.close();

        for (FeedParser.FeedItem item: itemMap.values()) {
            batch.add(ContentProviderOperation.newInsert(TedContentContract.FeedItem.CONTENT_URI)
                    .withValue(TedContentContract.FeedItem.LINK, item.mLink)
                    .withValue(TedContentContract.FeedItem.TITLE, item.mTitle)
                    .withValue(TedContentContract.FeedItem.CATEGORY, item.mCategory)
                    .withValue(TedContentContract.FeedItem.DESCRIPTION, item.mDescription)
                    .withValue(TedContentContract.FeedItem.PUB_DATE, item.mPubDate)
                    .withValue(TedContentContract.FeedItem.THUMBNAIL_LINK, item.mThumbnail)
                    .withValue(TedContentContract.FeedItem.VIDEO_LINK, item.mVideoLink)
                    .build());
            syncResult.stats.numInserts++;
        }

        mContentResolver.applyBatch(TedContentContract.AUTHORITY, batch);
        mContentResolver.notifyChange(TedContentContract.FeedItem.CONTENT_URI, null, false);
    }

    static InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
