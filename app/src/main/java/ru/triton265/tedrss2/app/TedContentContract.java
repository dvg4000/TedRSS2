package ru.triton265.tedrss2.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TedContentContract {
    public static final String AUTHORITY = "ru.triton265.tedrss2.provider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class FeedItem implements BaseColumns{
        static final String ITEMS = "items";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TedContentContract.CONTENT_URI, ITEMS);

        public static final String CONTENT_ITEM_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + AUTHORITY + FeedReaderContract.FeedItem.TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + AUTHORITY + FeedReaderContract.FeedItem.TABLE_NAME;

        /**
         * Link of rss item
         * <p>TYPE: TEXT</p>
         */
        public static final String LINK = FeedReaderContract.FeedItem.COLUMN_NAME_LINK;

        /**
         * Title
         * <p>TYPE: TEXT</p>
         */
        public static final String TITLE = FeedReaderContract.FeedItem.COLUMN_NAME_TITLE;

        /**
         * Description
         * <p>TYPE: TEXT</p>
         */
        public static final String DESCRIPTION = FeedReaderContract.FeedItem.COLUMN_NAME_DESCRIPTION;

        /**
         * Category
         * <p>TYPE: TEXT</p>
         */
        public static final String CATEGORY = FeedReaderContract.FeedItem.COLUMN_NAME_CATEGORY;

        /**
         * Publication date (unix time)
         * <p>TYPE: INTEGER</p>
         */
        public static final String PUB_DATE = FeedReaderContract.FeedItem.COLUMN_NAME_PUB_DATE;

        /**
         * Thumbnail link
         * <p>TYPE: TEXT</p>
         */
        public static final String THUMBNAIL_LINK = FeedReaderContract.FeedItem.COLUMN_NAME_THUMBNAIL_LINK;

        /**
         * Video link
         * <p>TYPE: TEXT</p>
         */
        public static final String VIDEO_LINK = FeedReaderContract.FeedItem.COLUMN_NAME_VIDEO_LINK;

        public static final String SORT_ORDER_DEFAULT = PUB_DATE + " DESC";
    }
}
