package br.com.ilhasoft.push.java_wrapper.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;

import br.com.ilhasoft.push.java_wrapper.adapters.GsonDateTypeAdapter;
import br.com.ilhasoft.push.java_wrapper.models.ApiResponse;
import br.com.ilhasoft.push.java_wrapper.models.Contact;
import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.FlowRun;
import br.com.ilhasoft.push.java_wrapper.models.Message;
import br.com.ilhasoft.push.java_wrapper.adapters.HashMapTypeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gualberto on 6/13/16.
 */
public class RapidProServices {

    private static final String TOKEN_FORMAT = "Token %s";

    private String token;
    private String host;

    private RapidProApi rapidProApi;
    private GsonDateTypeAdapter gsonDateTypeAdapter;

    public RapidProServices(String host, String token) {
        this.token = token;
        this.host = host;

        checkFields(token);
        buildApi();
    }

    private void checkFields(String token) {
        if (this.token != null && !token.startsWith("Token")) {
            this.token = String.format(TOKEN_FORMAT, token);
        }
    }

    private void buildApi() {
        gsonDateTypeAdapter = new GsonDateTypeAdapter();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, gsonDateTypeAdapter)
                .registerTypeAdapter(HashMap.class, new HashMapTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory (GsonConverterFactory.create(gson))
                .build();

        rapidProApi = retrofit.create(RapidProApi.class);
    }

    public Call<ApiResponse<FlowRun>> loadRuns(String userUuid, Date after) {
        return rapidProApi.listRuns(token, userUuid, gsonDateTypeAdapter.serializeDate(after));
    }

    public Call<FlowDefinition> loadFlowDefinition(String flowUuid) {
        return rapidProApi.loadFlowDefinition(token, flowUuid);
    }

    public Call<Contact> loadContact(String urn) {
        return rapidProApi.loadContact(token, urn);
    }

    public Call<ApiResponse<Contact>> loadContactsByUrn(String urn) {
        return rapidProApi.loadContacts(token, urn);
    }

    public Call<ResponseBody> sendReceivedMessage(String channel, String from, String msg) {
        return rapidProApi.sendReceivedMessage(token, channel, from, msg);
    }

    public Call<Contact> saveContact(Contact contact) {
        return rapidProApi.saveContact(token, contact);
    }

    public Call<ApiResponse<Message>> loadMessages(String contactUuid) {
        return rapidProApi.listMessages(token, contactUuid);
    }

    public Call<ApiResponse<Message>> loadMessageById(Integer messageId) {
        return rapidProApi.listMessageById(token, messageId);
    }

}
