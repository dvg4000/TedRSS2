package ru.triton265.tedrss2.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements FeedItemFragment.IObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FeedItemFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onItemClick(FeedParser.FeedItem item) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack("main")
                .replace(R.id.container, VideoFragment.newInstance(item))
                .commit();
    }
}
