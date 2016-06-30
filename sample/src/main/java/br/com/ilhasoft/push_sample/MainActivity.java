package br.com.ilhasoft.push_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.push.listeners.ContactListener;
import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.chat.ChatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact();
                contact.setEmail("johndcc@gmail.com");
                contact.setName("John Chat");
                IlhaPush.updateContact(contact, new ContactListener() {
                    @Override
                    public void onContactSaved(Contact contact) {
                        Log.d(TAG, "onContactSaved() called with: " + "contact = [" + contact + "]");
                    }

                    @Override
                    public void onError(Throwable exception, String message) {
                        Log.d(TAG, "onError() called with: " + "exception = [" + exception + "], message = [" + message + "]");
                    }
                });
            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ChatActivity.createIntent(MainActivity.this, R.mipmap.ic_launcher);
                startActivity(intent);
            }
        });
    }
}
