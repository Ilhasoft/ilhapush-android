package br.com.ilhasoft.push.listeners;

import br.com.ilhasoft.push.java_wrapper.models.Message;

/**
 * Created by john-mac on 6/29/16.
 */
public interface LoadMessageListener extends ErrorListener {

    void onMessageLoaded(Message message);

}
