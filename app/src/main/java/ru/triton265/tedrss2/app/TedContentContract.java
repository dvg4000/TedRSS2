package ru.triton265.tedrss2.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TedContentContract {
    public static final String AUTHORITY = "ru.triton265.tedrss2.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class FeedItem implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TedContentContract.CONTENT_URI,
                FeedReaderContract.FeedItem.TABLE_NAME);

        public static final String CONTENT_ITEM_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + AUTHORITY + FeedReaderContract.FeedItem.TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + AUTHORITY + FeedReaderContract.FeedItem.TABLE_NAME;
    }
}
