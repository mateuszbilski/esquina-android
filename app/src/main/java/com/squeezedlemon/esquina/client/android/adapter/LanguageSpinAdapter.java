package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.squeezedlemon.esquina.client.android.data.json.LanguageCode;

import java.util.List;

public class LanguageSpinAdapter extends ArrayAdapter<LanguageCode> {

    private Context context;

    private List<LanguageCode> values;

    private int spinnerResourceId;

    private int textViewResourceId;

    public LanguageSpinAdapter(Context context, int spinnerResourceId, int textViewResourceId, List<LanguageCode> values) {
        super(context, spinnerResourceId, textViewResourceId, values);
        this.spinnerResourceId = spinnerResourceId;
        this.textViewResourceId = textViewResourceId;
        this.context = context;
        this.values = values;
    }

    public List<LanguageCode> getValues() {
        return values;
    }

    public void setValues(List<LanguageCode> values) {
        this.values = values;
    }

}
