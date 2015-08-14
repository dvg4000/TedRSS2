package ru.triton265.tedrss2.app;

import android.os.Parcel;
import android.test.InstrumentationTestCase;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeedParserTest extends InstrumentationTestCase {
    private static final SimpleDateFormat mFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
            Locale.ENGLISH);

    public void testAssets() throws IOException, XmlPullParserException, ParseException {
        final InputStream stream = getInstrumentation().getContext().getAssets().open("tedtalks_video.xml");
        assertNotNull(stream);

        final FeedParser parser = new FeedParser();
        final List<FeedParser.FeedItem> feed = parser.parse(stream);

        assertNotNull(feed);
        assertEquals(99, feed.size());

        assertEquals("http://images.ted.com/images/ted/ddf471e870af8dbf908cdbc895239fccda769b48_480x360.jpg",
                feed.get(0).mThumbnail);

        assertEquals("http://images.ted.com/images/ted/cf733306649d209d53dc9cb18f8e5521ab6e70c8_480x360.jpg",
                feed.get(98).mThumbnail);

        final Long date1 = mFormatter.parse("Tue, 05 May 2015 15:15:01 +0000").getTime();
        assertEquals(date1, feed.get(1).mPubDate);

        final Long date96 = mFormatter.parse("Wed, 03 Dec 2014 16:04:32 +0000").getTime();
        assertEquals(date96, feed.get(96).mPubDate);

        assertEquals("http://images.ted.com/images/ted/ddf471e870af8dbf908cdbc895239fccda769b48_480x360.jpg",
                feed.get(0).mThumbnail);

        assertEquals("http://images.ted.com/images/ted/cf733306649d209d53dc9cb18f8e5521ab6e70c8_480x360.jpg",
                feed.get(98).mThumbnail);

        final String description0 = "Legendary dance choreographer Bill T. Jones and TED Fellows Joshua Roman and Somi didn't know exactly what was going to happen when they took the stage at TED2015. They just knew they wanted to offer the audience an opportunity to witness creative collaboration in action. The result: An improvised piece they call \"The Red Circle and the Blue Curtain,\" so extraordinary it had to be shared ...";
        assertEquals(description0, feed.get(0).mDescription);

        final String description1 = "To see is to believe, says Oren Yakobovich — which is why he helps everyday people use hidden cameras to film dangerous situations of violence, political fraud and abuse. His organization, Videre, uncovers, verifies and publicizes human-rights abuses that the world needs to witness.";
        assertEquals(description1, feed.get(98).mDescription);

        stream.close();
    }

    public void testUrl() throws IOException, ParseException, XmlPullParserException {
        final InputStream stream = SyncAdapter.downloadUrl(new URL(SyncAdapter.FEED_URL));
        final List<FeedParser.FeedItem> entries = new FeedParser().parse(stream);

        assertNotNull(entries);
        assertTrue(entries.size() > 0);

        for (FeedParser.FeedItem item: entries) { assertNotNull(item.mLink); }

        stream.close();
    }

    // http://stackoverflow.com/questions/12829700/android-unit-testing-bundle-parcelable
    public void testFeedItem() {
        final FeedParser.FeedItem item = new FeedParser.FeedItem("title", "description", "link",
                "category", 1000L, "video", "thumb");

        final Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        final FeedParser.FeedItem itemFromParcel = FeedParser.FeedItem.CREATOR.createFromParcel(parcel);
        assertEquals(item, itemFromParcel);
    }

    public void testFeedItemNull() {
        final String video = "video";
        final String description = "description";

        final FeedParser.FeedItem item = new FeedParser.FeedItem(null, description, null,
                null, null, video, null);

        final Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        final FeedParser.FeedItem itemFromParcel = FeedParser.FeedItem.CREATOR.createFromParcel(parcel);
        assertEquals(item, itemFromParcel);

        assertEquals(video, itemFromParcel.mVideoLink);
        assertEquals(description, itemFromParcel.mDescription);
    }
}
