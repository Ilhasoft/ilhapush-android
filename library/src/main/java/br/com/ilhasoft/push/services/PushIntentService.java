package br.com.ilhasoft.push.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;

import br.com.ilhasoft.push.chat.IlhaPushChatActivity;
import io.rapidpro.gcm.RapidProIntentService;

/**
 * Created by john-mac on 6/29/16.
 */
public class PushIntentService extends RapidProIntentService {

    public static final String EXTRA_DATA = "data";
    public static final String ACTION_MESSAGE_RECEIVED = "br.com.ilhasoft.push.MESSAGE_RECEIVED";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        String type = data.getString("type");
        if (isRapidproType(type)) {
            Intent pushReceiveIntent = new Intent(ACTION_MESSAGE_RECEIVED);
            pushReceiveIntent.putExtra(EXTRA_DATA, data);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushReceiveIntent);
        }
    }

    private boolean isRapidproType(String type) {
        return type != null && type.equals("Rapidpro");
    }

    @Override
    public final String handleNotificationMessage(String message) {
        return !TextUtils.isEmpty(message) ? Html.fromHtml(message).toString() : message;
    }

    @Override
    public void onCreateLocalNotification(NotificationCompat.Builder mBuilder) {
        mBuilder.setContentIntent(createPendingIntent());
        super.onCreateLocalNotification(mBuilder);
    }

    private PendingIntent createPendingIntent() {
        Intent chatIntent = new Intent(this, IlhaPushChatActivity.class);
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
