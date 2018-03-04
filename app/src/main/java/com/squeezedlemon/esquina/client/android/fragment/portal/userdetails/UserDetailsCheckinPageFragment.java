package com.squeezedlemon.esquina.client.android.fragment.portal.userdetails;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.adapter.UserCheckinListAdapter;
import com.squeezedlemon.esquina.client.android.data.LocationEntry;
import com.squeezedlemon.esquina.client.android.data.UserCheckinEntry;
import com.squeezedlemon.esquina.client.android.data.json.UserCheckin;

import java.util.LinkedList;
import java.util.List;

public class UserDetailsCheckinPageFragment extends Fragment {

    private FragmentData fragmentData;

    public static UserDetailsCheckinPageFragment newInstance(List<UserCheckin> checkins) {
        UserDetailsCheckinPageFragment fragment = new UserDetailsCheckinPageFragment();
        fragment.fragmentData = new FragmentData(checkins);
        return fragment;
    }

    public UserDetailsCheckinPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_details_checkin_page, container, false);

        List<UserCheckinEntry> userCheckinEntryList = new LinkedList<>();
        for (UserCheckin item : fragmentData.getCheckins()) {
            userCheckinEntryList.add(new UserCheckinEntry(
                    new LocationEntry(null, item.getLocation().getAddress(), item.getLocation().getId(), item.getLocation().getName()),
                    item.getScore(), item.getComment(), item.getDate()));
        }

        ListView listView = (ListView) v.findViewById(R.id.userCheckinListView);
        listView.setEmptyView(v.findViewById(R.id.userCheckinListEmptyTextView));
        listView.setAdapter(new UserCheckinListAdapter(getActivity(), userCheckinEntryList));

        return v;
    }

    private static class FragmentData {

        private List<UserCheckin> checkins;

        private FragmentData(List<UserCheckin> checkins) {
            this.checkins = checkins;
        }

        public List<UserCheckin> getCheckins() {
            return checkins;
        }

        public void setCheckins(List<UserCheckin> checkins) {
            this.checkins = checkins;
        }

    }
}
