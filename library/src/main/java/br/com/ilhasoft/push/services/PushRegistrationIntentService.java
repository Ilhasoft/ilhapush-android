package br.com.ilhasoft.push.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import br.com.ilhasoft.flowrunner.flow.ContactBuilder;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.persistence.Preferences;
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
            InstanceID instanceID = InstanceID.getInstance(this);
            String gcmId = instanceID.getToken(IlhaPush.getGcmSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            ContactBuilder contactBuilder = new ContactBuilder();
            contactBuilder.setGcmId(gcmId);

            Response<Contact> response = contactBuilder.saveContact(IlhaPush.getToken()).execute();
            Contact contact = response.body();
            contact.setPhone(null);

            Preferences preferences = IlhaPush.getPreferences();
            preferences.setGcmSenderId(IlhaPush.getGcmSenderId());
            preferences.setIdentity(gcmId);
            preferences.setContactUuid(contact.getUuid());
            preferences.apply();

            onGcmRegistered(gcmId, contact);
        } catch (Exception exception) {
            Log.e(TAG, "onHandleIntent: ", exception);
        }

        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    public void onGcmRegistered(String pushIdentity, Contact contact){}

}
