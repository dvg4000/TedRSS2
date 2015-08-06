package ru.triton265.tedrss2.app;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter{
    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = getContext().getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = getContext().getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult)
    {
        // TODO: implement;
        throw new UnsupportedOperationException();
    }
}
