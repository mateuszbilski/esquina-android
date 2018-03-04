package com.squeezedlemon.esquina.client.android.fragment.portal;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.adapter.LocationListAdapter;
import com.squeezedlemon.esquina.client.android.adapter.UserListAdapter;
import com.squeezedlemon.esquina.client.android.context.UserContext;
import com.squeezedlemon.esquina.client.android.data.LocationEntry;
import com.squeezedlemon.esquina.client.android.data.UserEntry;
import com.squeezedlemon.esquina.client.android.data.json.LocationData;
import com.squeezedlemon.esquina.client.android.data.json.UserData;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.userdetails.UserDetailsListPageFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;
import com.squeezedlemon.esquina.client.android.service.CloudResultReceiver;
import com.squeezedlemon.esquina.client.android.service.CloudService;
import com.squeezedlemon.esquina.client.android.util.AlertDialogUtils;
import com.squeezedlemon.esquina.client.android.util.Constants;
import com.squeezedlemon.esquina.client.android.util.ResponseEntityUtils;
import com.squeezedlemon.esquina.client.android.util.ResponseJsonError;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements CloudResultReceiver.Receiver {

    private static final String DIALOG_LOADER_TAG = "loader";

    /**
     * Receives result of executed service
     */
    private CloudResultReceiver receiver;

    private LoaderFragment loader;

    private Navigator navigator;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
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

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ((Button) v.findViewById(R.id.searchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        ((ListView) v.findViewById(R.id.searchResultListView)).setEmptyView(v.findViewById(R.id.searchNoResultFoundTextView));

        return v;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.searchTypeRadioGroup);
        boolean userSearchType = (radioGroup.getCheckedRadioButtonId() == R.id.searchUsersRadioButton);

        loader.dismiss();
        try {
            if (resultCode == CloudService.STATUS_CONNECTION_ERROR) {
                showAlertDialog(getString(R.string.connection_error_info));
                getView().findViewById(R.id.searchResultRelativeLayout).setVisibility(View.INVISIBLE);
            } else {
                Map<String, Object> responseMap = (Map<String, Object>) resultData.getSerializable(CloudService.RESPONSE_MAP);
                if ( ((HttpStatus) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_STATUS)).series().equals(HttpStatus.Series.SUCCESSFUL) ) {
                    populateResultList(responseMap, userSearchType);
                    getView().findViewById(R.id.searchResultRelativeLayout).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.searchResultRelativeLayout).setVisibility(View.INVISIBLE);
                    String errorMessage = new ResponseJsonError(getActivity(), (String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY))
                            .getUserFriendlyMessage();
                    showAlertDialog(errorMessage);
                }
            }
        } catch (JsonParsingException e) {
            e.printStackTrace();
            showAlertDialog(getString(R.string.cannot_parse_response_info));
            getView().findViewById(R.id.searchResultRelativeLayout).setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            receiver.setReceiver(null);
        }
    }

    private void performSearch() {
        String query = StringUtils.trimWhitespace(((EditText) getView().findViewById(R.id.searchQueryEditText)).getText().toString());
        if (query.length() >= 2) {
            receiver = new CloudResultReceiver(new Handler());
            receiver.setReceiver(this);

            //Show loading animation
            loader = new LoaderFragment();
            loader.addCancelListener(new LoaderFragment.CancelListener() {
                @Override
                public void canceled() {
                    cancelSearch();
                }
            });
            loader.show(getFragmentManager(), DIALOG_LOADER_TAG);

            //Perform intent
            HashMap<String, Object> queryParamsMap = new HashMap<>();
            queryParamsMap.put("query", query);

            String searchType;
            RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.searchTypeRadioGroup);
            if (radioGroup.getCheckedRadioButtonId() == R.id.searchUsersRadioButton) {
                searchType = "/search/user";
            } else {
                searchType = "/search/location";
            }

            Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
            intent.putExtra(CloudService.RECEIVER, receiver);
            intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat(searchType).concat("?query={query}"));
            intent.putExtra(CloudService.METHOD_TYPE, HttpMethod.GET.toString());
            intent.putExtra(CloudService.QUERY_PARAMS, queryParamsMap);
            intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
            intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
            getActivity().startService(intent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.searchQueryInvalid), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelSearch() {
        receiver.setReceiver(null);
        loader.dismiss();
        showAlertDialog(getString(R.string.request_canceled_text));
    }

    private void populateResultList(Map<String, Object> responseMap, boolean isUserSearchType) throws JsonParsingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readValue((String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY), JsonNode.class);

            if (isUserSearchType) {
                List<UserData> searchResult = mapper.convertValue(json.path("result"),
                        mapper.getTypeFactory().constructCollectionType(List.class, UserData.class));

                List<UserEntry> userEntryList = new LinkedList<>();
                for (UserData item : searchResult) {
                    userEntryList.add(new UserEntry(null, item.getAccountName(), item.getFirstName(),
                            item.getMiddleName(), item.getLastName()));
                }

                final ListView listView = (ListView) getView().findViewById(R.id.searchResultListView);
                listView.setAdapter(new UserListAdapter(getActivity(), userEntryList));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserEntry entry = ((UserListAdapter) listView.getAdapter()).getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putString(UserDetailsFragment.BUNDLE_ACCOUNT_NAME, entry.getAccountName());
                        navigator.navigate(SearchFragment.this, NavigatorEnum.USER_DETAILS, bundle);
                    }
                });
            } else {
                List<LocationData> searchResult = mapper.convertValue(json.path("result"),
                        mapper.getTypeFactory().constructCollectionType(List.class, LocationData.class));

                List<LocationEntry> locationEntryList = new LinkedList<>();
                for (LocationData item : searchResult) {
                    locationEntryList.add(new LocationEntry(null, item.getAddress(), item.getId(), item.getName()));
                }

                final ListView listView = (ListView) getView().findViewById(R.id.searchResultListView);
                listView.setAdapter(new LocationListAdapter(getActivity(), locationEntryList));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LocationEntry entry = ((LocationListAdapter) listView.getAdapter()).getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putLong(LocationDetailsFragment.BUNDLE_LOCATION_ID, entry.getId());
                        navigator.navigate(SearchFragment.this, NavigatorEnum.LOCATION_DETAILS, bundle);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonParsingException(e);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new JsonParsingException(ex);
        }
    }

    private void showAlertDialog(String message) {
        AlertDialogUtils.createAlertDialog(getActivity(), message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

}
