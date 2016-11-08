package br.com.ilhasoft.push.services;

import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.ilhasoft.push.IlhaPush;

/**
 * Created by john-mac on 6/28/16.
 */
public class PushInstanceIntentIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        IlhaPush.forceRegistration();
    }
}
