package ru.triton265.tedrss2.app;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    private FeedReaderContract() {}

    public static abstract class FeedItem implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_THUMBNAIL_LINK = "thumbnail_link";
        public static final String COLUMN_NAME_VIDEO_LINK = "video_link";
    }
}
