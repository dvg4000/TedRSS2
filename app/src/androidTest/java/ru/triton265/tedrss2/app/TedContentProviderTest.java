package ru.triton265.tedrss2.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class TedContentProviderTest extends ProviderTestCase2<TedContentProvider> {

    private MockContentResolver mResolver;

    public TedContentProviderTest() {
        super(TedContentProvider.class, TedContentContract.AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mResolver = getMockContentResolver();
    }

    public void testEmptyQuery() {
        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI, null, null, null, null);
        assertNotNull("cursor is null", cursor);
        assertEquals("cursor size is not 0", 0, cursor.getCount());
        cursor.close();
    }

    public void testInsert() {
        final String formatLink = "link %d";
        final String formatTitle = "title %d";
        final String formatDescription = "description %d";
        final String formatCategory = "category %d";
        final String formatThumbnail = "thumbnail %d";
        final String formatVideo = "video %d";

        final int rows = 10;
        final ContentValues values = new ContentValues();
        for (int i = 0; i < rows; i++) {
            values.clear();
            values.put(TedContentContract.FeedItem.LINK, String.format(formatLink, i));
            values.put(TedContentContract.FeedItem.TITLE, String.format(formatTitle, i));
            values.put(TedContentContract.FeedItem.DESCRIPTION, String.format(formatDescription, i));
            values.put(TedContentContract.FeedItem.CATEGORY, String.format(formatCategory, i));
            values.put(TedContentContract.FeedItem.THUMBNAIL_LINK, String.format(formatThumbnail, i));
            values.put(TedContentContract.FeedItem.VIDEO_LINK, String.format(formatVideo, i));
            values.put(TedContentContract.FeedItem.PUB_DATE, i);

            final Uri rowUri = mResolver.insert(TedContentContract.FeedItem.CONTENT_URI, values);
            assertNotNull(rowUri);
            assertEquals(String.valueOf(i + 1), rowUri.getLastPathSegment());
        }

        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI, null, null, null, null);
        assertNotNull("cursor is null", cursor);
        assertEquals("cursor size is not " + rows, rows, cursor.getCount());

        int i = 0;
        while (cursor.moveToNext()) {
            assertEquals("wrong link", String.format(formatLink, i),
                    cursor.getString(cursor.getColumnIndex(TedContentContract.FeedItem.LINK)));
            assertEquals("wrong pub date", i,
                    cursor.getInt(cursor.getColumnIndex(TedContentContract.FeedItem.PUB_DATE)));
            i++;
        }
        cursor.close();
    }

    public void testDelete() {
        final int rows = 10;
        final ContentValues values = new ContentValues();
        for (int i = 0; i < rows; i++) {
            values.clear();
            values.put(TedContentContract.FeedItem.PUB_DATE, i);
            mResolver.insert(TedContentContract.FeedItem.CONTENT_URI, values);
        }

        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI, null, null, null, null);
        assertNotNull("cursor is null", cursor);
        assertEquals("cursor size is not " + rows, rows, cursor.getCount());
        cursor.close();

        final int deletedRows = mResolver.delete(TedContentContract.FeedItem.CONTENT_URI, null, null);
        assertEquals("deleted rows is not " + rows, rows, deletedRows);

        final Cursor cursorDeleted = mResolver.query(TedContentContract.FeedItem.CONTENT_URI, null, null, null, null);
        assertNotNull("deleted cursor is null", cursorDeleted);
        assertEquals("deleted cursor size is not 0", 0, cursorDeleted.getCount());
        cursorDeleted.close();
    }

    public void testUpdate() {
        final String linkValue = "link";
        final ContentValues values = new ContentValues();
        values.put(TedContentContract.FeedItem.LINK, linkValue);
        mResolver.insert(TedContentContract.FeedItem.CONTENT_URI, values);

        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem._ID, TedContentContract.FeedItem.LINK },
                null, null, null);
        assertNotNull("cursor is null", cursor);
        assertTrue(cursor.moveToNext());
        assertEquals(linkValue, cursor.getString(1));
        final int rowId = cursor.getInt(0);
        cursor.close();

        final String linkNewValue = "new link value";
        values.clear();
        values.put(TedContentContract.FeedItem.LINK, linkNewValue);
        final int updatedRows = mResolver.update(TedContentContract.FeedItem.CONTENT_URI, values,
                TedContentContract.FeedItem._ID + " = ?", new String[]{String.valueOf(rowId)});
        assertEquals(1, updatedRows);

        final Cursor cursorUpdate = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem.LINK },
                null, null, null);
        assertNotNull("cursor is null", cursor);
        assertTrue(cursorUpdate.moveToNext());
        assertEquals(linkNewValue, cursorUpdate.getString(0));
        cursorUpdate.close();
    }

    public void testUpdate2() {
        final String linkValue = "link";
        final ContentValues values = new ContentValues();
        values.put(TedContentContract.FeedItem.LINK, linkValue);
        mResolver.insert(TedContentContract.FeedItem.CONTENT_URI, values);

        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem.LINK },
                null, null, null);
        assertNotNull("cursor is null", cursor);
        assertTrue(cursor.moveToNext());
        assertEquals(linkValue, cursor.getString(0));
        cursor.close();

        final String linkNewValue = "new link value";
        values.clear();
        values.put(TedContentContract.FeedItem.LINK, linkNewValue);
        final int updatedRows = mResolver.update(Uri.withAppendedPath(TedContentContract.FeedItem.CONTENT_URI, "/1"),
                values, null, null);
        assertEquals(1, updatedRows);

        final Cursor cursorUpdate = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem.LINK },
                null, null, null);
        assertNotNull("cursor is null", cursor);
        assertTrue(cursorUpdate.moveToNext());
        assertEquals(linkNewValue, cursorUpdate.getString(0));
        cursorUpdate.close();
    }

    public void testSort() {
        final int rows = 15;
        final ContentValues values = new ContentValues();
        for (int i = 0; i < rows; i++) {
            values.clear();
            values.put(TedContentContract.FeedItem.PUB_DATE, i);
            mResolver.insert(TedContentContract.FeedItem.CONTENT_URI, values);
        }

        final Cursor cursor = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem.PUB_DATE }, null, null, null);
        assertNotNull("cursor is null", cursor);
        assertEquals("cursor size is not " + rows, rows, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(0, cursor.getInt(0));
        cursor.close();

        final Cursor cursorLast = mResolver.query(TedContentContract.FeedItem.CONTENT_URI,
                new String[]{ TedContentContract.FeedItem.PUB_DATE }, null, null,
                TedContentContract.FeedItem._ID + " DESC");
        assertNotNull("cursor is null", cursor);
        assertEquals("cursor size is not " + rows, rows, cursorLast.getCount());
        assertTrue(cursorLast.moveToNext());
        assertEquals(rows - 1, cursorLast.getInt(0));
        cursorLast.close();
    }
}
