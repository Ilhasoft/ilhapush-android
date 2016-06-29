package br.com.ilhasoft.push_sample;

import br.com.ilhasoft.push.IlhaPush;

/**
 * Created by john-mac on 6/28/16.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IlhaPush.initialize(this, "Token bb207a615b7e6fae61c80e0d75a8975bc4e54815"
                , "4ff7908b-911e-434a-90ed-8ae62c629121"
                , "198991767403");
    }
}
