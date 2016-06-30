package br.com.ilhasoft.push_sample.services;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.com.ilhasoft.flowrunner.gcm.UdoIntentService;
import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.chat.ChatActivity;
import br.com.ilhasoft.push.services.PushIntentService;

/**
 * Created by john-mac on 6/29/16.
 */
public class ReceiverIntentService extends PushIntentService {

    private static final String TAG = "ReceiverIntentService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.d(TAG, "onMessageReceived() called with: " + "from = [" + from + "], data = [" + data + "]");
    }

    @Override
    public void onCreateLocalNotication(NotificationCompat.Builder mBuilder) {
        mBuilder.setContentTitle(getString(br.com.ilhasoft.push_sample.R.string.title_message));
        mBuilder.setSmallIcon(R.drawable.ic_send_message);
        super.onCreateLocalNotication(mBuilder);
    }
}
