package br.com.ilhasoft.push.listeners;

/**
 * Created by john-mac on 6/28/16.
 */
public interface ErrorListener {

    void onError(Throwable exception, String message);

}
