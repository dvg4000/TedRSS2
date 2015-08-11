package ru.triton265.tedrss2.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


public class FeedItemFragment extends ListFragment {

    private static final String[] PROJECTION = new String[]{
            TedContentContract.FeedItem._ID,
            TedContentContract.FeedItem.TITLE,
            TedContentContract.FeedItem.PUB_DATE,
            TedContentContract.FeedItem.THUMBNAIL_LINK
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TITLE = 1;
    private static final int COLUMN_PUB_DATE = 2;
    private static final int COLUMN_THUMBNAIL_LINK = 3;

    private static final String[] FROM_COLUMNS = new String[]{
            TedContentContract.FeedItem.TITLE,
            TedContentContract.FeedItem.PUB_DATE,
            TedContentContract.FeedItem.THUMBNAIL_LINK,
    };

    private static final int[] TO_FIELDS = new int[]{
            R.id.title,
            R.id.pubdate,
            R.id.thumbnail
    };

    private SimpleCursorAdapter mAdapter;


    public static FeedItemFragment newInstance() {
        FeedItemFragment fragment = new FeedItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.feed_list_item, null, FROM_COLUMNS, TO_FIELDS, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex) {
                    case COLUMN_PUB_DATE:
                        final String time = DateFormat.getDateTimeInstance()
                                .format(new Date(cursor.getLong(columnIndex)));
                        ((TextView) view).setText(time);
                        return true;
                    case COLUMN_THUMBNAIL_LINK:
                        // TODO: use glide here Luke.
                        return true;
                    default:
                        return false;
                }
            }
        });

        setListAdapter(mAdapter);
        // TODO: setEmptyText(getText(R.string.loading));
        // TODO: getLoaderManager().initLoader(0, null, this);
    }

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
    */

}
