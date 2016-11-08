package br.com.ilhasoft.push_sample;

import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.UiConfiguration;
import br.com.ilhasoft.push_sample.services.PushRegistrationService;

/**
 * Created by john-mac on 6/28/16.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IlhaPush.initialize(this, "Token 633567b35380a19c0e14556e9b2f702468b2f941"
                , "61421d58-7f77-418b-83c3-9cce0c8888c4", PushRegistrationService.class);
        IlhaPush.setUiConfiguration(new UiConfiguration()
                .setIconResource(R.mipmap.ic_launcher)
                .setTitleString("Sample Title"));
    }
}
