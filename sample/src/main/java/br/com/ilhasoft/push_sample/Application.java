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

        IlhaPush.initialize(this, "Token bb207a615b7e6fae61c80e0d75a8975bc4e54815"
                , "cd617363-da79-4920-a170-5def869928d5"
                , "198991767403", PushRegistrationService.class);
        IlhaPush.setUiConfiguration(new UiConfiguration()
                .setIconResource(R.mipmap.ic_launcher)
                .setTitleString("Sample Title"));
    }
}
