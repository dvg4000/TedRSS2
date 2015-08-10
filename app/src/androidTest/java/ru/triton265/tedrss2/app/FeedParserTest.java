package ru.triton265.tedrss2.app;

import android.test.InstrumentationTestCase;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeedParserTest extends InstrumentationTestCase {
    private static final SimpleDateFormat mFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
            Locale.ENGLISH);

    public void test() throws IOException, XmlPullParserException, ParseException {
        final InputStream stream = getInstrumentation().getContext().getAssets().open("tedtalks_video.xml");
        assertNotNull(stream);

        final FeedParser parser = new FeedParser();
        List<FeedParser.FeedItem> feed = parser.parse(stream);

        assertNotNull(feed);
        assertEquals(99, feed.size());

        assertEquals("http://images.ted.com/images/ted/ddf471e870af8dbf908cdbc895239fccda769b48_480x360.jpg",
                feed.get(0).mThumbnail);

        assertEquals("http://images.ted.com/images/ted/cf733306649d209d53dc9cb18f8e5521ab6e70c8_480x360.jpg",
                feed.get(98).mThumbnail);

        Long date1 = mFormatter.parse("Tue, 05 May 2015 15:15:01 +0000").getTime();
        assertEquals(date1, feed.get(1).mPubDate);

        Long date96 = mFormatter.parse("Wed, 03 Dec 2014 16:04:32 +0000").getTime();
        assertEquals(date96, feed.get(96).mPubDate);

        stream.close();
    }
}
