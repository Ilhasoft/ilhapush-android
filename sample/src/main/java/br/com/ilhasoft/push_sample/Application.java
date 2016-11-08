package br.com.ilhasoft.push_sample;

import br.com.ilhasoft.push.IlhaPush;
import br.com.ilhasoft.push.UiConfiguration;

/**
 * Created by john-mac on 6/28/16.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IlhaPush.initialize(new IlhaPush.Builder()
                            .setContext(this)
                            .setHost("https://push-staging.ilhasoft.mobi")
                            .setToken("633567b35380a19c0e14556e9b2f702468b2f941")
                            .setChannel("e3c213ad-428f-4064-8fc1-1f490d201f02")
                            .setUiConfiguration(new UiConfiguration()
                                    .setIconResource(R.mipmap.ic_launcher)
                                    .setTitleString("Sample Title")));
    }
}
