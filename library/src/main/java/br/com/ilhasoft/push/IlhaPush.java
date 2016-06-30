package br.com.ilhasoft.push;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import br.com.ilhasoft.flowrunner.models.ApiResponse;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
import br.com.ilhasoft.push.listeners.ContactListener;
import br.com.ilhasoft.push.listeners.LoadMessageListener;
import br.com.ilhasoft.push.listeners.MessagesLoadingListener;
import br.com.ilhasoft.push.listeners.SendMessageListener;
import br.com.ilhasoft.push.persistence.Preferences;
import br.com.ilhasoft.push.services.RegistrationIntentService;
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
    private static @DrawableRes int iconResource;

    private static Preferences preferences;
    private static RapidProServices services;

    IlhaPush() {}

    public static void initialize(Context context, String token, String channel, String gcmSenderId) {
        IlhaPush.context = context;
        IlhaPush.token = token;
        IlhaPush.channel = channel;
        IlhaPush.gcmSenderId = gcmSenderId;
        IlhaPush.preferences = new Preferences(context);
        IlhaPush.services = new RapidProServices(getToken());
        IlhaPush.iconResource = R.drawable.ic_send_message;

        registerGcmIfNeeded(context);
    }

    public static void setIconResource(int iconResource) {
        IlhaPush.iconResource = iconResource;
    }

    public static int getIconResource() {
        return iconResource;
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
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.error_message_send));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.error_message_send));
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
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.error_load_message));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable exception) {
                listener.onError(exception, context.getString(R.string.error_load_message));
            }
        });
    }

    public static void loadMessages(final MessagesLoadingListener listener) {
        RapidProServices services = new RapidProServices(getToken());
        services.loadMessages(getPreferences().getContactUuid()).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                if (response.isSuccessful()) {
                    listener.onMessagesLoaded(response.body().getResults());
                } else {
                    listener.onError(getExceptionForErrorResponse(response), context.getString(R.string.error_load_messages));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.error_load_messages));
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
                            , context.getString(R.string.error_contact_update));
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable throwable) {
                listener.onError(throwable, context.getString(R.string.error_contact_update));
            }
        });
    }

    @NonNull
    private static IllegalStateException getExceptionForErrorResponse(Response response) {
        return new IllegalStateException("Response not successful. HTTP Code: " + response.code() + " Response: " + response.raw());
    }

    private static void registerGcmIfNeeded(Context context) {
        if (TextUtils.isEmpty(preferences.getIdentity())) {
            Intent registrationIntent = RegistrationIntentService.createIntent(context);
            context.startService(registrationIntent);
        }
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
