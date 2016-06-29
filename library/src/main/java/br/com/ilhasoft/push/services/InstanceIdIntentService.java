package br.com.ilhasoft.push.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by john-mac on 6/28/16.
 */
public class InstanceIdIntentService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent registrationIntent = RegistrationIntentService.createIntent(this);
        startService(registrationIntent);
    }
}
