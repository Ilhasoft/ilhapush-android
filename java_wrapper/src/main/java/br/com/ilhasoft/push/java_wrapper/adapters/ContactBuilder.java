package br.com.ilhasoft.push.java_wrapper.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ilhasoft.push.java_wrapper.models.Contact;
import br.com.ilhasoft.push.java_wrapper.network.RapidProServices;
import retrofit2.Call;

/**
 * Created by john-mac on 11/7/16.
 */
public class ContactBuilder {

    private Contact contact;

    public ContactBuilder() {
        contact = new Contact();
    }

    public ContactBuilder setFcmId(final String fcmId) {
        contact.setUrns(new ArrayList<String>() {{
            add("fcm:" + fcmId);
        }});
        return this;
    }

    public ContactBuilder setGroups(final List<String> groups) {
        contact.setGroups(groups);
        return this;
    }

    public ContactBuilder setEmail(final String email) {
        contact.setEmail(email);
        return this;
    }

    public ContactBuilder setName(final String name) {
        contact.setName(name);
        return this;
    }

    public ContactBuilder setFields(HashMap<String, Object> fields) {
        contact.setFields(fields);
        return this;
    }

    public Call<Contact> saveContact(String host, String token) {
        RapidProServices rapidProServices = new RapidProServices(host, token);
        return rapidProServices.saveContact(contact);
    }
}
