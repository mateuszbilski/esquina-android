package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.UserCheckinEntry;

import java.text.SimpleDateFormat;
import java.util.List;

public class UserCheckinListAdapter extends ArrayAdapter<UserCheckinEntry> {

    private final Context context;
    private final List<UserCheckinEntry> userCheckinEntryList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public UserCheckinListAdapter(Context context, List<UserCheckinEntry> userCheckinEntryList) {
        super(context, R.layout.item_user_checkin_list, userCheckinEntryList);
        this.context = context;
        this.userCheckinEntryList = userCheckinEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserCheckinEntry entry = userCheckinEntryList.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_user_checkin_list, parent, false);

        ((TextView) rowView.findViewById(R.id.locationNameTextView)).setText(entry.getLocationEntry().getName());
        ((TextView) rowView.findViewById(R.id.locationAddressTextView)).setText(entry.getLocationEntry().getFriendlyAddress());
        ((TextView) rowView.findViewById(R.id.checkinDate)).setText(dateFormat.format(entry.getCheckinDate()));

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
