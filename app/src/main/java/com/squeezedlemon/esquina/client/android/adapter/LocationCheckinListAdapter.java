package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.LocationCheckinEntry;

import java.text.SimpleDateFormat;
import java.util.List;

public class LocationCheckinListAdapter extends ArrayAdapter<LocationCheckinEntry> {

    private final Context context;
    private final List<LocationCheckinEntry> locationCheckinEntryList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public LocationCheckinListAdapter(Context context, List<LocationCheckinEntry> locationCheckinEntryList) {
        super(context, R.layout.item_location_checkin_list, locationCheckinEntryList);
        this.context = context;
        this.locationCheckinEntryList = locationCheckinEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocationCheckinEntry entry = locationCheckinEntryList.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_location_checkin_list, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.userNameTextView);
        textView.setText(entry.getUserEntry().getUserDescription());
        ((TextView) rowView.findViewById(R.id.checkinDate)).setText(dateFormat.format(entry.getDate()));

        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.checkinScore);
        if (entry.getScore() != null) {
            ratingBar.setRating(entry.getScore().floatValue());
        } else {
            ratingBar.setVisibility(View.INVISIBLE);
        }

        if (entry.getComment() != null) {
            ((TextView) rowView.findViewById(R.id.checkinComment)).setText(entry.getComment());
        } else {
            rowView.findViewById(R.id.checkinComment).setVisibility(View.GONE);
        }

        return rowView;
    }
}
