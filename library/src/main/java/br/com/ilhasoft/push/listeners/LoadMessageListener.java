package br.com.ilhasoft.push.listeners;

import io.rapidpro.models.Message;

/**
 * Created by john-mac on 6/29/16.
 */
public interface LoadMessageListener extends ErrorListener {

    void onMessageLoaded(Message message);

}
