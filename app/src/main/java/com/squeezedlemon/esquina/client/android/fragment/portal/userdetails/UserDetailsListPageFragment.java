package com.squeezedlemon.esquina.client.android.fragment.portal.userdetails;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.adapter.UserListAdapter;
import com.squeezedlemon.esquina.client.android.data.UserEntry;
import com.squeezedlemon.esquina.client.android.data.json.UserData;
import com.squeezedlemon.esquina.client.android.fragment.portal.UserDetailsFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class UserDetailsListPageFragment extends Fragment {

    private Navigator navigator;

    private FragmentData fragmentData;

    public static UserDetailsListPageFragment newInstance(List<UserData> userDataList,
                                                               String emptyListMessage) {
        UserDetailsListPageFragment fragment = new UserDetailsListPageFragment();
        fragment.fragmentData = new FragmentData(userDataList, emptyListMessage);
        return fragment;
    }

    public UserDetailsListPageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            navigator = (Navigator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement Navigator.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigator = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<UserEntry> userEntryList = new LinkedList<>();
        for (UserData item : fragmentData.getFollowingList()) {
            userEntryList.add(new UserEntry(null, item.getAccountName(), item.getFirstName(),
                    item.getMiddleName(), item.getLastName()));
        }


        View v = inflater.inflate(R.layout.view_user_list, container, false);
        ((TextView) v.findViewById(R.id.userListEmptyTextView)).setText(fragmentData.getEmptyListMessage());
        final ListView listView = (ListView) v.findViewById(R.id.userListView);
        listView.setEmptyView(v.findViewById(R.id.userListEmptyTextView));
        listView.setAdapter(new UserListAdapter(getActivity(), userEntryList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserEntry entry = ((UserListAdapter) listView.getAdapter()).getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString(UserDetailsFragment.BUNDLE_ACCOUNT_NAME, entry.getAccountName());
                navigator.navigate(UserDetailsListPageFragment.this, NavigatorEnum.USER_DETAILS, bundle);
            }
        });

        return v;
    }

    private static class FragmentData implements Serializable {

        private List<UserData> followingList;

        private String emptyListMessage;

        private FragmentData(List<UserData> followingList, String emptyListMessage) {
            this.followingList = followingList;
            this.emptyListMessage = emptyListMessage;
        }

        public List<UserData> getFollowingList() {
            return followingList;
        }

        public void setFollowingList(List<UserData> followingList) {
            this.followingList = followingList;
        }

        public String getEmptyListMessage() {
            return emptyListMessage;
        }

        public void setEmptyListMessage(String emptyListMessage) {
            this.emptyListMessage = emptyListMessage;
        }
    }
}
