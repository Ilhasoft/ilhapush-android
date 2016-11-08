package br.com.ilhasoft.push.java_wrapper.models;

/**
 * Created by john-mac on 6/29/16.
 */
public class Channel {

    private String uuid;

    private String name;

    public String getUuid() {
        return uuid;
    }

    public Channel setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Channel setName(String name) {
        this.name = name;
        return this;
    }
}
