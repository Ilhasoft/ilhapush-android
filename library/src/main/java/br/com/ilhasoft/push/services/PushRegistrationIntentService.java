package br.com.ilhasoft.push.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.java_wrapper.models.Contact;
import br.com.ilhasoft.push.persistence.Preferences;
import br.com.ilhasoft.push.java_wrapper.adapters.ContactBuilder;
import retrofit2.Response;

/**
 * Created by john-mac on 6/27/16.
 */
public class PushRegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntent";

    public PushRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String pushToken = FirebaseInstanceId.getInstance().getToken();

            Preferences preferences = IlhaPush.getPreferences();
            preferences.setIdentity(pushToken);

            Contact contact = null;
            if (!TextUtils.isEmpty(IlhaPush.getToken())) {
                Response<Contact> response = saveContactWithToken(pushToken, IlhaPush.getToken());
                contact = response.body();
                contact.setPhone(null);
                preferences.setContactUuid(contact.getUuid());
            }

            preferences.apply();
            onGcmRegistered(pushToken, contact);
        } catch (Exception exception) {
            Log.e(TAG, "onHandleIntent: ", exception);
        }

        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private Response<Contact> saveContactWithToken(String gcmId, String token) throws java.io.IOException {
        ContactBuilder contactBuilder = new ContactBuilder();
        contactBuilder.setFcmId(gcmId);

        return contactBuilder.saveContact(token).execute();
    }

    public void onGcmRegistered(String pushIdentity, Contact contact){}

}
