package br.com.ilhasoft.push.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import br.com.ilhasoft.push.R;
import br.com.ilhasoft.push.UiConfiguration;
import br.com.ilhasoft.push.chat.tags.OnTagClickListener;
import br.com.ilhasoft.push.chat.tags.TagsAdapter;
import br.com.ilhasoft.push.services.PushIntentService;
import br.com.ilhasoft.push.util.BundleHelper;

/**
 * Created by john-mac on 8/30/16.
 */
public class IlhaPushChatFragment extends Fragment implements ChatView {

    private EditText message;
    private RecyclerView messageList;
    private RecyclerView tags;
    private ProgressBar progressBar;

    private ChatMessagesAdapter adapter;

    private ChatPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ilhapush_fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);

        presenter = new ChatPresenter(this);
        presenter.loadMessages();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        message = (EditText) view.findViewById(R.id.message);
        adapter = new ChatMessagesAdapter();

        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , 5, getResources().getDisplayMetrics());

        SpaceItemDecoration messagesItemDecoration = new SpaceItemDecoration();
        messagesItemDecoration.setVerticalSpaceHeight(spacing);

        messageList = (RecyclerView) view.findViewById(R.id.messageList);
        messageList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        messageList.addItemDecoration(messagesItemDecoration);
        messageList.setAdapter(adapter);

        SpaceItemDecoration tagsItemDecoration = new SpaceItemDecoration();
        tagsItemDecoration.setHorizontalSpaceWidth(spacing);

        tags = (RecyclerView) view.findViewById(R.id.tags);
        tags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tags.addItemDecoration(tagsItemDecoration);

        ImageView sendMessage = (ImageView) view.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(onSendMessageClickListener);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(Toolbar toolbar) {
        if (getSupportActionBar() == null) {
            toolbar.setVisibility(View.VISIBLE);

            int titleColorRes = IlhaPush.getUiConfiguration().getTitleColor();
            toolbar.setTitle(IlhaPush.getUiConfiguration().getTitleString());
            toolbar.setTitleTextColor(getResources().getColor(titleColorRes));
            toolbar.setBackgroundColor(getToolbarColor());
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }else {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(getToolbarColor()));
            setActionBarTitleColor(actionBar, IlhaPush.getUiConfiguration().getTitleString());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(IlhaPush.getUiConfiguration().getBackResource());
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void setActionBarTitleColor(ActionBar actionBar, String title){
        Spannable text = new SpannableString(title);
        int titleColorRes = IlhaPush.getUiConfiguration().getTitleColor();
        text.setSpan(new ForegroundColorSpan(getResources().getColor(titleColorRes)), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(text);
    }

    @ColorInt
    private int getToolbarColor() {
        int toolbarColorResource = IlhaPush.getUiConfiguration().getToolbarColor();
        return toolbarColorResource == UiConfiguration.INVALID_COLOR ? fetchColorPrimary() :
                getResources().getColor(toolbarColorResource);
    }

    private int fetchColorPrimary() {
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter messagesBroadcastFilter = new IntentFilter(PushIntentService.ACTION_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(messagesReceiver, messagesBroadcastFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(messagesReceiver);
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                message.setError(getString(R.string.ilhapush_error_send_message));
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