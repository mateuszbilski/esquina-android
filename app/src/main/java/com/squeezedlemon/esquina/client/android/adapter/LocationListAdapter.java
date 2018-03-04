package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.LocationEntry;

import java.util.List;

public class LocationListAdapter extends ArrayAdapter<LocationEntry> {

    private final Context context;
    private final List<LocationEntry> locationEntryList;

    public LocationListAdapter(Context context, List<LocationEntry> locationEntryList) {
        super(context, R.layout.item_location_list, locationEntryList);
        this.context = context;
        this.locationEntryList = locationEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocationEntry entry = locationEntryList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_location_list, parent, false);
        ((TextView) rowView.findViewById(R.id.locationNameTextView)).setText(entry.getName());
        ((TextView) rowView.findViewById(R.id.locationAddressTextView)).setText(entry.getFriendlyAddress());

        return rowView;
    }
}
