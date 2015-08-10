package ru.triton265.tedrss2.app;

import android.support.annotation.NonNull;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FeedParser {
    private static final String XML_NAMESPACE = null;
    private static final String XML_NAMESPACE_MEDIA = "http://search.yahoo.com/mrss/";

    private static final String RSS_CHANNEL = "channel";
    private static final String RSS_ITEM = "item";
    private static final String RSS_TITLE = "title";
    private static final String RSS_DESCRIPTION = "description";
    private static final String RSS_LINK = "link";
    private static final String RSS_CATEGORY = "category";
    private static final String RSS_PUB_DATE = "pubDate";
    private static final String RSS_VIDEO = "content";
    private static final String RSS_VIDEO_URL = "url";
    private static final String RSS_THUMBNAIL = "thumbnail";
    private static final String RSS_THUMBNAIL_URL = "url";

    private static final SimpleDateFormat mFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
            Locale.ENGLISH);


    public List<FeedItem> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            final XmlPullParser parser = Xml.newPullParser();
            //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            return findItem(parser, RSS_CHANNEL) ? readFeed(parser) : Collections.EMPTY_LIST;
        } finally {
            in.close();
        }
    }

    private List<FeedItem> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        final List<FeedItem> entries = new ArrayList<FeedItem>();

        parser.require(XmlPullParser.START_TAG, XML_NAMESPACE, RSS_CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals(RSS_ITEM)) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private FeedItem readEntry(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        String title = null;
        String description = null;
        String link = null;
        String category = null;
        String video = null;
        String thumbnail = null;
        Long pubdate = null;

        parser.require(XmlPullParser.START_TAG, XML_NAMESPACE, RSS_ITEM);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            switch (parser.getName()) {
                case RSS_TITLE:
                    title = readText(parser);
                    break;
                case RSS_DESCRIPTION:
                    description = readText(parser);
                    break;
                case RSS_LINK:
                    link = readText(parser);
                    break;
                case RSS_PUB_DATE:
                    pubdate = mFormatter.parse(readText(parser)).getTime();
                    break;
                case RSS_CATEGORY:
                    category = readText(parser);
                    break;
                case RSS_VIDEO:
                    video = readAttribute(parser, RSS_VIDEO, XML_NAMESPACE_MEDIA, RSS_VIDEO_URL);
                    break;
                case RSS_THUMBNAIL:
                    thumbnail = readAttribute(parser, RSS_THUMBNAIL, XML_NAMESPACE_MEDIA, RSS_THUMBNAIL_URL);
                    break;
                default:
                    skip(parser);
            }
        }
        return new FeedItem(title, description, link, category, pubdate, video, thumbnail);
    }

    private String readAttribute(XmlPullParser parser, String name, String ns, String attr)
            throws IOException, XmlPullParserException
    {
        String url = null;
        parser.require(XmlPullParser.START_TAG, ns, name);
        url = parser.getAttributeValue(null, attr);
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, name);
        return url;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private boolean findItem(@NonNull XmlPullParser parser, @NonNull String item) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static class FeedItem {
        public final String mTitle;
        public final String mDescription;
        public final String mLink;
        public final String mCategory;
        public final String mThumbnail;
        public final String mVideoLink;
        public final Long mPubDate;

        public FeedItem(String title, String description, String link, String category,
                        Long pubDate, String videoLink, String thumbnail)
        {
            mTitle = title;
            mDescription = description;
            mLink = link;
            mCategory = category;
            mPubDate = pubDate;
            mThumbnail = thumbnail;
            mVideoLink = videoLink;
        }
    }
}
