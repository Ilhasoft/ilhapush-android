package br.com.ilhasoft.push_sample.services;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.services.PushIntentService;

/**
 * Created by john-mac on 6/29/16.
 */
public class ReceiverIntentService extends PushIntentService {

    @Override
    public void onCreateLocalNotification(NotificationCompat.Builder mBuilder) {
        mBuilder.setSmallIcon(R.drawable.ilhapush_ic_send_message);
        super.onCreateLocalNotification(mBuilder);
    }
}
