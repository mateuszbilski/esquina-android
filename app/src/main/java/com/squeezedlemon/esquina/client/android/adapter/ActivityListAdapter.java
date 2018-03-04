package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.ActivityEntry;

import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityListAdapter extends ArrayAdapter<ActivityEntry> {

    private final Context context;
    private final List<ActivityEntry> activityEntryList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public ActivityListAdapter(Context context, List<ActivityEntry> activityEntryList) {
        super(context, R.layout.item_activity_list, activityEntryList);
        this.context = context;
        this.activityEntryList = activityEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ActivityEntry entry = activityEntryList.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_activity_list, parent, false);

        ((TextView) rowView.findViewById(R.id.userNameTextView)).setText(entry.getUserEntry().getUserDescription().concat(" ")
                .concat(context.getString(R.string.hasCheckedIn)));
        ((TextView) rowView.findViewById(R.id.locationNameTextView)).setText(entry.getUserCheckinEntry().getLocationEntry().getName());
        ((TextView) rowView.findViewById(R.id.locationAddressTextView)).setText(entry.getUserCheckinEntry().getLocationEntry().getFriendlyAddress());
        ((TextView) rowView.findViewById(R.id.checkinDate)).setText(dateFormat.format(entry.getUserCheckinEntry().getCheckinDate()));

        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.checkinScore);
        if (entry.getUserCheckinEntry().getScore() != null) {
            ratingBar.setRating(entry.getUserCheckinEntry().getScore().floatValue());
        } else {
            ratingBar.setVisibility(View.INVISIBLE);
        }

        if (entry.getUserCheckinEntry().getComment() != null) {
            ((TextView) rowView.findViewById(R.id.checkinComment)).setText(entry.getUserCheckinEntry().getComment());
        } else {
            rowView.findViewById(R.id.checkinComment).setVisibility(View.GONE);
        }

        return rowView;
    }
}
