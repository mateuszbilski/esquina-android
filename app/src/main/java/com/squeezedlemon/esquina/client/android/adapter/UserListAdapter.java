package com.squeezedlemon.esquina.client.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.UserEntry;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<UserEntry> {

    private final Context context;
    private final List<UserEntry> userEntryList;

    public UserListAdapter(Context context, List<UserEntry> userEntryList) {
        super(context, R.layout.item_user_list, userEntryList);
        this.context = context;
        this.userEntryList = userEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_user_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.userNameTextView);
        textView.setText(userEntryList.get(position).getUserDescription());

        return rowView;
    }
}
