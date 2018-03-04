package com.squeezedlemon.esquina.client.android.data.json;

import java.io.Serializable;

public class LocationTag implements Serializable {

    private String tag;
    private String icon;
    private String translation;
    private String language;

    public LocationTag() {

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
