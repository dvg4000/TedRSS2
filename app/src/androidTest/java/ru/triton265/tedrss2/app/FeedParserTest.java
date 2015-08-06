package ru.triton265.tedrss2.app;

import android.test.InstrumentationTestCase;

import java.io.IOException;
import java.io.InputStream;

public class FeedParserTest extends InstrumentationTestCase {

    public void test() throws IOException {
        final InputStream stream = getInstrumentation().getContext().getAssets().open("tedtalks_video.xml");
        assertNotNull(stream);
        stream.close();
    }
}
