package br.com.ilhasoft.push.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.MessagesLoadingListener;
import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.services.PushIntentService;

/**
 * Created by john-mac on 6/27/16.
 */
public class ChatActivity extends AppCompatActivity {

    private EditText message;

    private ChatMessagesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupView();
        loadMessages();
    }

    private void setupView() {
        message = (EditText) findViewById(R.id.message);

        adapter = new ChatMessagesAdapter(IlhaPush.getContact());

        RecyclerView messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
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
            loadMessages();
        }
    };

    private View.OnClickListener onSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = message.getText().toString();
            if (!messageText.isEmpty()) {
                message.setError(null);
                message.setText(null);
                IlhaPush.sendMessage(messageText);
            } else {
                message.setError(getString(R.string.error_send_message));
            }
        }
    };
}