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

        IlhaPush.initialize(new IlhaPush.Builder(this)
                            .setHost("http://push-staging.ilhasoft.mobi/")
                            .setToken("786cf3068e587315c6592a4883090b5de74069ff")
                            .setChannel("43231717-982f-433f-932e-26791eb285a2")
                            .setRegistrationServiceClass(PushRegistrationService.class)
                            .setUiConfiguration(new UiConfiguration()
                                    .setIconResource(R.mipmap.ic_launcher)
                                    .setTitleString("RapidCon")));
    }
}
