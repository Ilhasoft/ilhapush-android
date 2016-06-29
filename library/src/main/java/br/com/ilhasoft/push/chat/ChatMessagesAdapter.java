package br.com.ilhasoft.push.chat;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.Message;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> chatMessages;
    private Contact contact;

    private ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener;

    public ChatMessagesAdapter(Contact contact) {
        this.chatMessages = new ArrayList<>();
        this.contact = contact;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageViewHolder chatMessageViewHolder = ((ChatMessageViewHolder)holder);
        chatMessageViewHolder.setOnChatMessageSelectedListener(onChatMessageSelectedListener);
        chatMessageViewHolder.bindView(contact, chatMessages.get(position));
    }

    @Override
    public long getItemId(int position) {
        Message chatMessage = chatMessages.get(position);
        return chatMessage.getId() != null ? chatMessage.getId().hashCode() : 0;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setMessages(List<Message> messages) {
        this.chatMessages = messages;
        notifyDataSetChanged();
    }

    public void addChatMessage(Message message) {
        chatMessages.add(0, message);
        notifyItemInserted(0);
    }

    public void removeChatMessage(Message message) {
        int indexOfMessage = chatMessages.indexOf(message);
        if(indexOfMessage >= 0) {
            chatMessages.remove(indexOfMessage);
            notifyItemRemoved(indexOfMessage);
        }
    }

    public void setOnChatMessageSelectedListener(ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
