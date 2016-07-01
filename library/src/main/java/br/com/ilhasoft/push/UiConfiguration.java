package br.com.ilhasoft.push;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by john-mac on 7/1/16.
 */
public class UiConfiguration {

    @DrawableRes
    private int backResource = R.drawable.ic_arrow_back_white;

    @DrawableRes
    private int iconResource = R.drawable.ic_send_message;

    @ColorRes
    private int toolbarColor = R.attr.colorPrimary;

    private String titleString = "IlhaPush";

    public int getBackResource() {
        return backResource;
    }

    public UiConfiguration setBackResource(int backResource) {
        this.backResource = backResource;
        return this;
    }

    public int getIconResource() {
        return iconResource;
    }

    public UiConfiguration setIconResource(int iconResource) {
        this.iconResource = iconResource;
        return this;
    }

    public int getToolbarColor() {
        return toolbarColor;
    }

    public UiConfiguration setToolbarColor(int toolbarColor) {
        this.toolbarColor = toolbarColor;
        return this;
    }

    public String getTitleString() {
        return titleString;
    }

    public UiConfiguration setTitleString(String titleString) {
        this.titleString = titleString;
        return this;
    }
}
