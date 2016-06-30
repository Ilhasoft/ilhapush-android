package br.com.ilhasoft.push.util;

import android.os.Bundle;

/**
 * Created by john-mac on 6/29/16.
 */
public class BundleHelper {

    private static final String EXTRA_MESSAGE_ID = "message_id";

    public static Integer getMessageId(Bundle data) {
        return Integer.valueOf(data.getString(EXTRA_MESSAGE_ID, "0"));
    }

}
