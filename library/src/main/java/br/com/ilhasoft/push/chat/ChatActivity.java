package br.com.ilhasoft.push.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.flowrunner.views.manager.SpaceItemDecoration;
import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.listeners.LoadMessageListener;
import br.com.ilhasoft.push.listeners.MessagesLoadingListener;
import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.services.PushIntentService;
import br.com.ilhasoft.push.util.BundleHelper;

/**
 * Created by john-mac on 6/27/16.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private static final String EXTRA_ICON = "icon";

    private int icon;

    private EditText message;
    private RecyclerView messageList;
    private ChatMessagesAdapter adapter;

    public static Intent createIntent(Context context, @DrawableRes int icon) {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra(EXTRA_ICON, icon);
        return chatIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        icon = getIntent().getIntExtra(EXTRA_ICON, R.drawable.ic_send_message);

        setupView();
        loadMessages();
    }

    private void setupView() {
        message = (EditText) findViewById(R.id.message);
        adapter = new ChatMessagesAdapter(icon);

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , 5, getResources().getDisplayMetrics()));

        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messageList.addItemDecoration(spaceItemDecoration);
        messageList.setAdapter(adapter);

        ImageView sendMessage = (ImageView) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(onSendMessageClickListener);
    }

    private void loadMessages() {
        IlhaPush.loadMessages(new MessagesLoadingListener() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                adapter.setMessages(messages);
            }

            @Override
            public void onError(Throwable exception, String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter messagesBroadcastFilter = new IntentFilter(PushIntentService.ACTION_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(messagesReceiver, messagesBroadcastFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messagesReceiver);
    }

    private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra(PushIntentService.EXTRA_DATA);
            loadMessage(data);
        }
    };

    private void loadMessage(Bundle data) {
        IlhaPush.loadMessage(BundleHelper.getMessageId(data), new LoadMessageListener() {
            @Override
            public void onMessageLoaded(Message message) {
                adapter.addChatMessage(message);
                messageList.scrollToPosition(0);
            }

            @Override
            public void onError(Throwable exception, String message) {
                Log.d(TAG, "onError() called with: " + "exception = [" + exception + "], message = [" + message + "]");
            }
        });
    }

    private View.OnClickListener onSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = message.getText().toString();
            if (!messageText.isEmpty()) {
                addNewMessage(messageText);
                IlhaPush.sendMessage(messageText);
            } else {
                message.setError(getString(R.string.error_send_message));
            }
        }
    };

    private void addNewMessage(String messageText) {
        adapter.addChatMessage(createChatMessage(messageText));
        message.setError(null);
        message.setText(null);
        messageList.scrollToPosition(0);
    }

    private Message createChatMessage(String messageText) {
        Message chatMessage = new Message();
        chatMessage.setText(messageText);
        chatMessage.setCreatedOn(new Date());
        chatMessage.setDirection(Message.DIRECTION_INCOMING);
        return chatMessage;
    }
}