package ru.triton265.tedrss2.app;


import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


// TODO: add layout_land
public class VideoFragment extends Fragment {
    private static final String ARG_FEED_ITEM = "feed_item";
    private static final String BUNDLE_CURRENT_POSITION = "current_position";

    private VideoView mVideoView;
    private FeedParser.FeedItem mFeedItem;
    private int mCurrentPosition = -1;

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
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        if (null != savedInstanceState) {
            mCurrentPosition = savedInstanceState.getInt(BUNDLE_CURRENT_POSITION);
        }

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mVideoView = (VideoView)rootView.findViewById(R.id.videoView);
        mVideoView.setMediaController(new MediaController(getActivity()));
        mVideoView.setVideoURI(Uri.parse(mFeedItem.mVideoLink));
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                if (-1 != mCurrentPosition) { mVideoView.seekTo(mCurrentPosition); }
                mVideoView.start();
            }
        });

        final TextView description = (TextView)rootView.findViewById(R.id.description);
        if (null != description) {
            description.setText(mFeedItem.mDescription);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentPosition = mVideoView.getCurrentPosition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CURRENT_POSITION, mCurrentPosition);
    }
}
