package ru.triton265.tedrss2.app;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    public static final String ACCOUNT_TYPE = "tedrss2.triton265.ru";
    public static final String ACCOUNT_NAME = "guest";

    private StubAuthenticator mAuthenticator;

    public static Account getAccount() {
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new StubAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
