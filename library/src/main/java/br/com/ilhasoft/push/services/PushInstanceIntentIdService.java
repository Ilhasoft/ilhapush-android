package br.com.ilhasoft.push.services;

import com.google.android.gms.iid.InstanceIDListenerService;

import br.com.ilhasoft.push.IlhaPush;

/**
 * Created by john-mac on 6/28/16.
 */
public class PushInstanceIntentIdService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        IlhaPush.forceRegistration();
    }
}
