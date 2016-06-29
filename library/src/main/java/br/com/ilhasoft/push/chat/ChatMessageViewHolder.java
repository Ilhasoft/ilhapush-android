package br.com.ilhasoft.push.chat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;

import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.push.R;

/**
 * Created by john-mac on 4/11/16.
 */
public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private Message chatMessage;

    private ViewGroup parent;

    private TextView message;
    private TextView date;
    private TextView name;

    private OnChatMessageSelectedListener onChatMessageSelectedListener;

    private final DateFormat hourFormatter;
    private final Context context;

    public ChatMessageViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false));
        this.context = context;
        this.hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        this.parent = (ViewGroup) itemView.findViewById(R.id.bubble);
        this.name = (TextView) itemView.findViewById(R.id.name);

        this.itemView.setOnLongClickListener(onLongClickListener);
    }

    public void bindView(Contact contact, Message chatMessage) {
        this.chatMessage = chatMessage;
        bindContainer(contact, chatMessage);

        message = (TextView) findIfNeeded(message, R.id.chatMessage);
        message.setText(chatMessage.getText());

        date = (TextView) findIfNeeded(date, R.id.chatMessageDate);
        date.setText(hourFormatter.format(chatMessage.getCreatedOn()));
    }

    private void bindContainer(Contact contact, Message chatMessage) {
        boolean userAuthor = isUserAuthor(chatMessage);
        int smallSpace = getDpDimen(10);
        int largeSpace = getDpDimen(40);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        addRule(userAuthor, params);
        params.leftMargin = userAuthor ? largeSpace : smallSpace;
        params.rightMargin = userAuthor ? smallSpace : largeSpace;
        parent.setLayoutParams(params);

        int drawable = userAuthor ? R.drawable.bubble_me : R.drawable.bubble_other;
        parent.setBackgroundResource(drawable);

        name.setVisibility(userAuthor ? View.GONE : View.VISIBLE);
        name.setText(contact.getName());
    }

    private boolean isUserAuthor(Message chatMessage) {
        return chatMessage.getDirection().equals(Message.DIRECTION_INCOMING);
    }

    private void addRule(boolean userAuthor, RelativeLayout.LayoutParams params) {
        int rule;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            rule = userAuthor ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_PARENT_START;
        } else {
            rule = userAuthor ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT;
        }
        params.addRule(rule);
    }

    private int getDpDimen(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value
                , context.getResources().getDisplayMetrics());
    }

    private View findIfNeeded(View view, @IdRes int id) {
        if(view == null) {
            return itemView.findViewById(id);
        }
        return view;
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(onChatMessageSelectedListener != null) {
                onChatMessageSelectedListener.onChatMessageSelected(chatMessage);
            }
            return false;
        }
    };

    public void setOnChatMessageSelectedListener(OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    public interface OnChatMessageSelectedListener {
        void onChatMessageSelected(Message chatMessage);
    }
}
