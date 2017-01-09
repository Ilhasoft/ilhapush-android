package br.com.ilhasoft.push.listeners;

import io.rapidpro.models.Contact;

/**
 * Created by john-mac on 6/28/16.
 */
public interface ContactListener extends ErrorListener {

    void onContactSaved(Contact contact);

}
