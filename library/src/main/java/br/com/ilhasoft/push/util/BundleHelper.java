package br.com.ilhasoft.push.util;

import android.os.Bundle;

import io.rapidpro.models.Message;

/**
 * Created by john-mac on 6/29/16.
 */
public class BundleHelper {

    private static final String EXTRA_MESSAGE_ID = "message_id";
    private static final String EXTRA_MESSAGE = "message";

    public static Integer getMessageId(Bundle data) {
        return Integer.valueOf(data.getString(EXTRA_MESSAGE_ID, "0"));
    }

    public static String getMessageText(Bundle data) {
        return data.getString(EXTRA_MESSAGE);
    }

    public static Message getMessage(Bundle data) {
        Message message = new Message();
        message.setId(getMessageId(data));
        message.setText(getMessageText(data));
        message.setDirection(Message.DIRECTION_OUTGOING);
        return message;
    }

}
