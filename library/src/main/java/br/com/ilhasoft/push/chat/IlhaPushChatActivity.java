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
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import br.com.ilhasoft.flowrunner.managers.FlowRunnerManager;
import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.flowrunner.models.Type;
import br.com.ilhasoft.flowrunner.views.manager.SpaceItemDecoration;
import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.chat.tags.OnTagClickListener;
import br.com.ilhasoft.push.chat.tags.TagsAdapter;
import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.services.PushIntentService;
import br.com.ilhasoft.push.util.BundleHelper;

/**
 * Created by john-mac on 6/27/16.
 */
public class IlhaPushChatActivity extends AppCompatActivity implements ChatView {

    private EditText message;
    private RecyclerView messageList;
    private RecyclerView tags;
    private ProgressBar progressBar;

    private ChatMessagesAdapter adapter;

    private ChatPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupView();

        presenter = new ChatPresenter(this);
        presenter.loadMessages();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        message = (EditText) findViewById(R.id.message);
        adapter = new ChatMessagesAdapter();

        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , 5, getResources().getDisplayMetrics());

        SpaceItemDecoration messagesItemDecoration = new SpaceItemDecoration();
        messagesItemDecoration.setVerticalSpaceHeight(spacing);

        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messageList.addItemDecoration(messagesItemDecoration);
        messageList.setAdapter(adapter);

        SpaceItemDecoration tagsItemDecoration = new SpaceItemDecoration();
        tagsItemDecoration.setHorizontalSpaceWidth(spacing);

        tags = (RecyclerView) findViewById(R.id.tags);
        tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tags.addItemDecoration(tagsItemDecoration);

        ImageView sendMessage = (ImageView) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(onSendMessageClickListener);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(Toolbar toolbar) {
        if (getSupportActionBar() == null) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setBackgroundColor(getResources().getColor(IlhaPush.getUiConfiguration().getToolbarColor()));
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(IlhaPush.getUiConfiguration().getBackResource());
        getSupportActionBar().setTitle(IlhaPush.getUiConfiguration().getTitleString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
            presenter.loadMessage(data);

            Message message = BundleHelper.getMessage(data);
            message.setCreatedOn(new Date());
            adapter.addChatMessage(message);
            onLastMessageChanged(message);
        }
    };

    @Override
    public void onMessagesLoaded(List<Message> messages) {
        adapter.setMessages(messages);
        onLastMessageChanged(adapter.getLastMessage());
    }

    @Override
    public void onMessageLoaded(Message message) {
        adapter.addChatMessage(message);
        onLastMessageChanged(message);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void onLastMessageChanged(Message lastMessage) {
        messageList.scrollToPosition(0);
        if (lastMessage != null && lastMessage.getRuleset() != null
        && lastMessage.getRuleset().getRules() != null) {
            Type type = presenter.getFirstType(lastMessage);
            if (type == Type.Choice) {
                TagsAdapter tagsAdapter = new TagsAdapter(lastMessage.getRuleset().getRules(), onTagClickListener);
                tags.setAdapter(tagsAdapter);
                tags.setVisibility(View.VISIBLE);
            } else {
                message.setInputType(FlowRunnerManager.getInputTypeByType(type));
                tags.setVisibility(View.GONE);
            }
        } else {
            tags.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(IlhaPushChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Message getLastMessage() {
        return adapter.getLastMessage();
    }

    private View.OnClickListener onSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = message.getText().toString();
            if (!messageText.isEmpty()) {
                presenter.sendMessage(messageText);
            } else {
                message.setError(getString(R.string.error_send_message));
            }
        }
    };

    @Override
    public void addNewMessage(String messageText) {
        restoreView();

        adapter.addChatMessage(presenter.createChatMessage(messageText));
        messageList.scrollToPosition(0);
    }

    private void restoreView() {
        message.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        message.setError(null);
        message.setText(null);
        tags.setVisibility(View.GONE);
    }

    private OnTagClickListener onTagClickListener = new OnTagClickListener() {
        @Override
        public void onTagClick(String reply) {
            presenter.sendMessage(reply);
        }
    };
}