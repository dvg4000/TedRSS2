package ru.triton265.tedrss2.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class FeedItemFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    private static final long SYNC_FREQUENCY = TimeUnit.HOURS.convert(6, TimeUnit.HOURS);

    private static final String[] PROJECTION = new String[]{
            TedContentContract.FeedItem._ID,
            TedContentContract.FeedItem.TITLE,
            TedContentContract.FeedItem.PUB_DATE,
            TedContentContract.FeedItem.THUMBNAIL_LINK,
            TedContentContract.FeedItem.VIDEO_LINK,
            TedContentContract.FeedItem.DESCRIPTION
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TITLE = 1;
    private static final int COLUMN_PUB_DATE = 2;
    private static final int COLUMN_THUMBNAIL_LINK = 3;
    private static final int COLUMN_VIDEO_LINK = 4;
    private static final int COLUMN_DESCRIPTION = 5;

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
    private Menu mOptionsMenu;
    private Object mSyncObserverHandle;
    private IObserver mObserver;


    public static FeedItemFragment newInstance() {
        FeedItemFragment fragment = new FeedItemFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        createSyncAccount();

        try {
            mObserver = (IObserver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IObserver");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mObserver = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                        Glide.with(FeedItemFragment.this)
                                .load(cursor.getString(columnIndex))
                                //.fitCenter()
                                .centerCrop()
                                .placeholder(android.R.drawable.stat_sys_download)
                                .into((ImageView)view);
                        return true;
                    default:
                        return false;
                }
            }
        });

        setListAdapter(mAdapter);
        setEmptyText(getText(R.string.loading));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mSyncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TedContentContract.FeedItem.CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mOptionsMenu = menu;
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                triggerRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null == mObserver) { return; }

        final Cursor cursor = (Cursor)mAdapter.getItem(position);
        final FeedParser.FeedItem item = new FeedParser.FeedItem(
                null, cursor.getString(COLUMN_DESCRIPTION), null, null, null,
                cursor.getString(COLUMN_VIDEO_LINK),null);
        mObserver.onItemClick(item);
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.action_refresh);
        if (refreshItem != null) {
            refreshItem.setEnabled(!refreshing);
        }
    }

    private void createSyncAccount() {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getBoolean(PREF_SETUP_COMPLETE, false);

        final Account account = AuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, TedContentContract.AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, TedContentContract.AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, TedContentContract.AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            triggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    private void triggerRefresh() {
        final Bundle bundle = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                AuthenticatorService.getAccount(), // Sync account
                TedContentContract.AUTHORITY, // Content authority
                bundle);
    }

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Account account = AuthenticatorService.getAccount();
                    if (null == account) {
                        setRefreshActionButtonState(false);
                        return;
                    }
                    boolean syncActive = ContentResolver.isSyncActive(account, TedContentContract.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(account, TedContentContract.AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    interface IObserver {
        void onItemClick(FeedParser.FeedItem item);
    }
}
