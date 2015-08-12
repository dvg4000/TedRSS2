package ru.triton265.tedrss2.app;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// TODO: add layout_land
public class VideoFragment extends Fragment {
    private static final String ARG_FEED_ITEM = "feed_item";

    private FeedParser.FeedItem mFeedItem;

    public static VideoFragment newInstance(FeedParser.FeedItem feedItem) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FEED_ITEM, feedItem);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFeedItem = getArguments().getParcelable(ARG_FEED_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }
}
