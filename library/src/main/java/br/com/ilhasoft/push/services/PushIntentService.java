package br.com.ilhasoft.push.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;

import br.com.ilhasoft.flowrunner.gcm.UdoIntentService;
import br.com.ilhasoft.push.chat.IlhaPushChatActivity;

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

    @Override
    public final String handleNotificationMessage(String message) {
        return !TextUtils.isEmpty(message) ? Html.fromHtml(message).toString() : message;
    }

    @Override
    public void onCreateLocalNotication(NotificationCompat.Builder mBuilder) {
        mBuilder.setContentIntent(createPendingIntent());
        super.onCreateLocalNotication(mBuilder);
    }

    private PendingIntent createPendingIntent() {
        Intent chatIntent = new Intent(this, IlhaPushChatActivity.class);
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
