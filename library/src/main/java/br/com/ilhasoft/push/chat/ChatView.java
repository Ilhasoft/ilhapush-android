package br.com.ilhasoft.push.chat;

import java.util.List;

import br.com.ilhasoft.flowrunner.models.Message;

/**
 * Created by john-mac on 6/30/16.
 */
public interface ChatView {

    void addNewMessage(String messageText);

    void onMessagesLoaded(List<Message> messages);

    void onMessageLoaded(Message message);

    void showLoading();

    void dismissLoading();

    void showMessage(String message);

    Message getLastMessage();

}
