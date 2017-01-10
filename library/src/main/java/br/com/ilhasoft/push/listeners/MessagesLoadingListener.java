package br.com.ilhasoft.push.listeners;

import java.util.List;

import io.rapidpro.models.Message;

/**
 * Created by john-mac on 6/28/16.
 */
public interface MessagesLoadingListener extends ErrorListener {

    void onMessagesLoaded(List<Message> messages);

}
