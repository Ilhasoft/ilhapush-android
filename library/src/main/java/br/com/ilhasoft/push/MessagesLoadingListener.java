package br.com.ilhasoft.push;

import java.util.List;

import br.com.ilhasoft.flowrunner.models.Message;

/**
 * Created by john-mac on 6/28/16.
 */
public interface MessagesLoadingListener extends ErrorListener {

    void onMessagesLoaded(List<Message> messages);

}
