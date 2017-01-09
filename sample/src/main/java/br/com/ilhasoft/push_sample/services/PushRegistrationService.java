package br.com.ilhasoft.push_sample.services;

import android.util.Log;

import java.io.IOException;

import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.services.PushRegistrationIntentService;
import io.rapidpro.models.Contact;

/**
 * Created by john-mac on 7/2/16.
 */
public class PushRegistrationService extends PushRegistrationIntentService {

    private static final String TAG = "PushRegistrationService";

    @Override
    public void onGcmRegistered(String pushIdentity, Contact contact) {
        contact.setName("IlhaPush Sample User");
        contact.setEmail("sample@gmail.com");

        try {
            IlhaPush.updateContact(contact);
        } catch (IOException e) {
            Log.e(TAG, "onGcmRegistered: ", e);
        }
    }
}
