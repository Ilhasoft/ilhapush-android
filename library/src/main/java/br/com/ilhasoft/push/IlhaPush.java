package br.com.ilhasoft.push;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;

import br.com.ilhasoft.flowrunner.flow.ContactBuilder;
import br.com.ilhasoft.flowrunner.models.ApiResponse;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
import br.com.ilhasoft.push.chat.IlhaPushChatActivity;
import br.com.ilhasoft.push.chat.IlhaPushChatFragment;
import br.com.ilhasoft.push.listeners.ContactListener;
import br.com.ilhasoft.push.listeners.LoadMessageListener;
import br.com.ilhasoft.push.listeners.MessagesLoadingListener;
import br.com.ilhasoft.push.listeners.SendMessageListener;
import br.com.ilhasoft.push.persistence.Preferences;
import br.com.ilhasoft.push.services.PushRegistrationIntentService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by john-mac on 6/27/16.
 */
public class IlhaPush {

    private static Context context;
    private static String token;
    private static String channel;
    private static String gcmSenderId;
    private static Boolean forceRegistration = false;
    private static Class<? extends PushRegistrationIntentService> registrationServiceClass;
    private static UiConfiguration uiConfiguration;

    private static Preferences preferences;
    private static RapidProServices services;

    IlhaPush() {}

    public static void initialize(Context context, String token, String channel, String gcmSenderId
        , Class<? extends PushRegistrationIntentService> registrationServiceClass) {
        IlhaPush.context = context;
        IlhaPush.token = token;
        IlhaPush.channel = channel;
        IlhaPush.gcmSenderId = gcmSenderId;
        IlhaPush.registrationServiceClass = registrationServiceClass;
        IlhaPush.preferences = new Preferences(context);
        IlhaPush.services = new RapidProServices(getToken());
        IlhaPush.uiConfiguration = new UiConfiguration();

        registerGcmIfNeeded();
    }

    public static void initialize(Context context, String token, String channel, String gcmSenderId) {
        initialize(context, token, channel, gcmSenderId, PushRegistrationIntentService.class);
    }

    public static void initialize(Context context, String gcmSenderId, Class<? extends PushRegistrationIntentService> registrationServiceClass) {
        initialize(context, null, null, gcmSenderId, registrationServiceClass);
    }

    public static void initialize(Context context) {
        IlhaPush.context = context;
        IlhaPush.preferences = new Preferences(context);
        IlhaPush.uiConfiguration = new UiConfiguration();
    }

    public static UiConfiguration getUiConfiguration() {
        return uiConfiguration;
    }

    public static void startIlhaPushChatActivity(Context context) {
        startIlhaPushChatActivity(context, token, channel);
    }

    public static IlhaPushChatFragment getIlhaPushChatFragment(String token, String channel) {
        IlhaPush.token = token;
        IlhaPush.channel = channel;
        IlhaPush.services = new RapidProServices(token);
        return new IlhaPushChatFragment();
    }

    public static void startIlhaPushChatActivity(Context context, String token, String channel) {
        IlhaPush.token = token;
        IlhaPush.channel = channel;
        IlhaPush.services = new RapidProServices(token);
        context.startActivity(new Intent(context, IlhaPushChatActivity.class));
    }

    public static void setUiConfiguration(UiConfiguration uiConfiguration) {
        IlhaPush.uiConfiguration = uiConfiguration;
    }

    public static void sendMessage(String message) {
        services.sendReceivedMessage(channel, preferences.getIdentity(), message).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    public static void sendMessage(String message, final SendMessageListener listener) {
        services.sendReceivedMessage(channel, preferences.getIdentity(), message).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onSendMessage();
                } else {
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.ilhapush_error_message_send));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.ilhapush_error_message_send));
            }
        });
    }

    public static void loadMessage(Integer messageId, final LoadMessageListener listener) {
        RapidProServices services = new RapidProServices(getToken());
        services.loadMessageById(messageId).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                if (response.isSuccessful() && response.body().getResults() != null
                && !response.body().getResults().isEmpty()) {
                    listener.onMessageLoaded(response.body().getResults().get(0));
                } else {
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.ilhapush_error_load_message));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable exception) {
                listener.onError(exception, context.getString(R.string.ilhapush_error_load_message));
            }
        });
    }

    public static void loadMessages(final MessagesLoadingListener listener) {
        final RapidProServices services = new RapidProServices(getToken());
        String contactUuid = getPreferences().getContactUuid();
        if (!TextUtils.isEmpty(contactUuid)) {
            loadMessagesWithContact(services, contactUuid, listener);
        } else {
            loadContact(listener, services);
        }
    }

    private static void loadContact(final MessagesLoadingListener listener, final RapidProServices services) {
        services.loadContactsByUrn("gcm:" + getPreferences().getIdentity()).enqueue(new Callback<ApiResponse<Contact>>() {
            @Override
            public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                if (response.isSuccessful() && response.body().getCount() > 0) {
                    Contact contact = response.body().getResults().get(0);
                    loadMessagesWithContact(services, contact.getUuid(), listener);
                } else {
                    listener.onError(getExceptionForErrorResponse(response)
                            , context.getString(R.string.ilhapush_error_load_messages));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Contact>> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.ilhapush_error_load_messages));
            }
        });
    }

    private static void loadMessagesWithContact(RapidProServices services, String contactUuid, final MessagesLoadingListener listener) {
        services.loadMessages(contactUuid).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                if (response.isSuccessful()) {
                    listener.onMessagesLoaded(response.body().getResults());
                } else {
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.ilhapush_error_load_messages));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.ilhapush_error_load_messages));
            }
        });
    }

    public static Contact getContact() {
        Contact contact = new Contact();
        contact.setUuid(preferences.getContactUuid());
        return contact;
    }


    public static void updateContact(final Contact contact, final ContactListener listener) {
        contact.setUuid(preferences.getContactUuid());

        RapidProServices services = new RapidProServices(token);
        services.saveContact(contact).enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if (response.isSuccessful()) {
                    listener.onContactSaved(response.body());
                } else {
                    listener.onError(getExceptionForErrorResponse(response)
                            , context.getString(R.string.ilhapush_error_contact_update));
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.ilhapush_error_contact_update));
            }
        });
    }

    public static void updateContact(final Contact contact) throws IOException {
        contact.setUuid(preferences.getContactUuid());

        RapidProServices services = new RapidProServices(token);
        services.saveContact(contact).execute();
    }

    public static Response<Contact> saveContactWithToken(String gcmId, String token) throws java.io.IOException {
        ContactBuilder contactBuilder = new ContactBuilder();
        contactBuilder.setGcmId(gcmId);

        return contactBuilder.saveContact(token).execute();
    }

    public static void forceRegistration() {
        context.startService(new Intent(context, registrationServiceClass));
    }

    @NonNull
    private static IllegalStateException getExceptionForErrorResponse(Response response) {
        return new IllegalStateException("Response not successful. HTTP Code: " + response.code() + " Response: " + response.raw());
    }

    private static void registerGcmIfNeeded() {
        if (forceRegistration || TextUtils.isEmpty(preferences.getIdentity())) {
            forceRegistration();
        }
    }

    public static void setForceRegistration(Boolean forceRegistration) {
        IlhaPush.forceRegistration = forceRegistration;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static Context getContext() {
        return context;
    }

    public static String getToken() {
        return token;
    }

    public static String getChannel() {
        return channel;
    }

    public static String getGcmSenderId() {
        return gcmSenderId;
    }
}
