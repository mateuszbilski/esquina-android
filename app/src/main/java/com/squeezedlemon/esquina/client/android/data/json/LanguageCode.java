package com.squeezedlemon.esquina.client.android.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Class is used for mapping JSON property langCode into POJO
 */
public class LanguageCode implements Serializable {

    private String langCode;

    @JsonProperty("unified")
    private String unifiedDescription;

    @JsonProperty("local")
    private String localDescription;

    public LanguageCode() {

    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getUnifiedDescription() {
        return unifiedDescription;
    }

    public void setUnifiedDescription(String unifiedDescription) {
        this.unifiedDescription = unifiedDescription;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    public void setLocalDescription(String localDescription) {
        this.localDescription = localDescription;
    }

    @Override
    public String toString() {

        return localDescription;
    }
}
