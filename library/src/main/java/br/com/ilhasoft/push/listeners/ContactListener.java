package br.com.ilhasoft.push.listeners;

import br.com.ilhasoft.push.java_wrapper.models.Contact;

/**
 * Created by john-mac on 6/28/16.
 */
public interface ContactListener extends ErrorListener {

    void onContactSaved(Contact contact);

}
