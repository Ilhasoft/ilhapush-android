package br.com.ilhasoft.push.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import br.com.ilhasoft.flowrunner.gcm.UdoIntentService;

/**
 * Created by john-mac on 6/29/16.
 */
public class PushIntentService extends UdoIntentService {

    public static final String EXTRA_DATA = "data";
    public static final String ACTION_MESSAGE_RECEIVED = "br.com.ilhasoft.push.MESSAGE_RECEIVED";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Intent pushReceiveIntent = new Intent(ACTION_MESSAGE_RECEIVED);
        pushReceiveIntent.putExtra(EXTRA_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushReceiveIntent);
    }
}
