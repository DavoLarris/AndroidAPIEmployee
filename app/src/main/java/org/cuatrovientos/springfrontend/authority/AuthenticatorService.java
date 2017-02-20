package org.cuatrovientos.springfrontend.authority;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by David on 19/02/2017.
 */

public class AuthenticatorService extends Service {
    private Authenticator authenticator;

    @Override
    public void onCreate() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
