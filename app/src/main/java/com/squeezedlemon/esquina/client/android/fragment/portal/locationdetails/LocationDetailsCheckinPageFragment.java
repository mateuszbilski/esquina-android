package com.squeezedlemon.esquina.client.android.fragment.portal.locationdetails;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.adapter.LocationCheckinListAdapter;
import com.squeezedlemon.esquina.client.android.data.LocationCheckinEntry;
import com.squeezedlemon.esquina.client.android.data.UserEntry;
import com.squeezedlemon.esquina.client.android.data.json.LocationCheckin;

import java.util.LinkedList;
import java.util.List;

public class LocationDetailsCheckinPageFragment extends Fragment {

    private FragmentData fragmentData;

    public static LocationDetailsCheckinPageFragment newInstance(List<LocationCheckin> locationCheckinList) {
        LocationDetailsCheckinPageFragment fragment = new LocationDetailsCheckinPageFragment();
        fragment.fragmentData = new FragmentData(locationCheckinList);
        return fragment;
    }

    public LocationDetailsCheckinPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<LocationCheckinEntry> locationCheckinEntryList = new LinkedList<>();
        for (LocationCheckin item : fragmentData.getLocationCheckinList()) {
            locationCheckinEntryList.add(new LocationCheckinEntry(new UserEntry(null, item.getUser().getAccountName(),
                    item.getUser().getFirstName(), item.getUser().getMiddleName(), item.getUser().getLastName()),
                     item.getScore(), item.getComment(), item.getDate()));
        }

        View v = inflater.inflate(R.layout.fragment_location_details_checkin_page, container, false);
        ListView listView = (ListView) v.findViewById(R.id.locationCheckinListView);
        listView.setEmptyView(v.findViewById(R.id.locationCheckinListEmptyTextView));
        listView.setAdapter(new LocationCheckinListAdapter(getActivity(), locationCheckinEntryList));

        return v;
    }

    private static class FragmentData {

        private List<LocationCheckin> locationCheckinList;

        private FragmentData(List<LocationCheckin> locationCheckinList) {
            this.locationCheckinList = locationCheckinList;
        }

        public List<LocationCheckin> getLocationCheckinList() {
            return locationCheckinList;
        }

        public void setLocationCheckinList(List<LocationCheckin> locationCheckinList) {
            this.locationCheckinList = locationCheckinList;
        }
    }
}
