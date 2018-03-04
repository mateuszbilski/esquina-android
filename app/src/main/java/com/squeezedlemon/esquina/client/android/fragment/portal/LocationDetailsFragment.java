package com.squeezedlemon.esquina.client.android.fragment.portal;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.context.UserContext;
import com.squeezedlemon.esquina.client.android.data.json.LocationData;
import com.squeezedlemon.esquina.client.android.data.json.UserData;
import com.squeezedlemon.esquina.client.android.data.json.UserDetails;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.portal.locationdetails.LocationDetailsCheckinPageFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.locationdetails.LocationDetailsMainPageFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.userdetails.UserDetailsListPageFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.userdetails.UserDetailsMainPageFragment;
import com.squeezedlemon.esquina.client.android.service.CloudResultReceiver;
import com.squeezedlemon.esquina.client.android.service.CloudService;
import com.squeezedlemon.esquina.client.android.util.Constants;
import com.squeezedlemon.esquina.client.android.util.ResponseEntityUtils;
import com.squeezedlemon.esquina.client.android.util.ResponseJsonError;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LocationDetailsFragment extends Fragment implements CloudResultReceiver.Receiver {

    public static final String BUNDLE_LOCATION_ID = "locationId";

    @IdRes
    private static final int VIEW_PAGER_ID = 10001;
    public static final int VIEW_LOADER_CHILD_NUMBER = 0;
    public static final int VIEW_CONNECTION_PROBLEM_CHILD_NUMBER = 1;
    public static final int VIEW_PAGER_CHILD_NUMBER = 2;

    private static final ViewGroup.LayoutParams LAYOUT_PARAMS = new ViewGroup.LayoutParams
            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    /**
     * UIs
     */
    private ViewPager viewPager;
    private ViewAnimator viewAnimator;

    /**
     * Reference to location's id actually present
     */
    private Long locationId;

    /**
     * Reference to preloaded data from cloud
     */
    private Map<String, Object> responseMap;

    /**
     * Receives result of executed service
     */
    private CloudResultReceiver receiver;

    /**
     * On fragment creation, pass Bundle object with entry:
     * (locationId) -> location's id, that you want to present
     */
    public static LocationDetailsFragment newInstance(Bundle bundle) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        fragment.locationId = bundle.getLong(BUNDLE_LOCATION_ID);
        return fragment;
    }

    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        receiver = new CloudResultReceiver(new Handler());
        receiver.setReceiver(this);

        View view = inflater.inflate(R.layout.fragment_view_animator, container, false);

        viewAnimator = (ViewAnimator) view.findViewById(R.id.rootContainer);
        viewAnimator.addView(inflater.inflate(R.layout.view_loader, viewAnimator, false), VIEW_LOADER_CHILD_NUMBER, LAYOUT_PARAMS);

        View connectionProblemView = inflater.inflate(R.layout.view_connection_problem, viewAnimator, false);
        ((Button) connectionProblemView.findViewById(R.id.retryButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryButtonAction(v);
            }
        });
        viewAnimator.addView(connectionProblemView, VIEW_CONNECTION_PROBLEM_CHILD_NUMBER, LAYOUT_PARAMS);

        viewPager = new ViewPager(getActivity());
        viewPager.setId(VIEW_PAGER_ID);

        startLoadingData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            receiver.setReceiver(null);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        boolean showErrorPage = false;
        String errorMessage = getString(R.string.connection_error_info);

        try {
            if (resultCode == CloudService.STATUS_OK) {
                responseMap = (Map<String, Object>) resultData.getSerializable(CloudService.RESPONSE_MAP);
                if (HttpStatus.OK.equals(responseMap.get(ResponseEntityUtils.RESPONSE_MAP_STATUS))) {
                    prepareViewPager();
                    viewAnimator.setDisplayedChild(VIEW_PAGER_CHILD_NUMBER);
                } else {
                    showErrorPage = true;
                    errorMessage = new ResponseJsonError(getActivity(), (String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY))
                            .getUserFriendlyMessage();
                }
            } else if (resultCode == CloudService.STATUS_CONNECTION_ERROR) {
                showErrorPage = true;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (JsonParsingException e) {
            e.printStackTrace();
            showErrorPage = true;
            errorMessage = getString(R.string.cannot_parse_response_info);
        }

        if (showErrorPage) {
            viewAnimator.setDisplayedChild(VIEW_CONNECTION_PROBLEM_CHILD_NUMBER);
            ((TextView) viewAnimator.getCurrentView().findViewById(R.id.connectionProblemTextView)).setText(errorMessage);
        }
    }

    private void startLoadingData() {
        viewAnimator.setDisplayedChild(VIEW_LOADER_CHILD_NUMBER);

        HashMap<String, Object> queryParamMap = new HashMap<>();
        queryParamMap.put("locationId", locationId);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
        intent.putExtra(CloudService.RECEIVER, receiver);
        intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat("/location/{locationId}"));
        intent.putExtra(CloudService.METHOD_TYPE, HttpMethod.GET.toString());
        intent.putExtra(CloudService.QUERY_PARAMS, queryParamMap);
        intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
        intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
        getActivity().startService(intent);
    }

    private void prepareViewPager() throws JsonParsingException {
        ObjectMapper mapper = new ObjectMapper();
        if (viewAnimator != null && viewAnimator.getChildAt(VIEW_PAGER_CHILD_NUMBER) == null) {
            LocationData locationData = null;
            try {
                locationData = mapper.readValue((String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY), LocationData.class);
            } catch (IllegalArgumentException ex) {
                throw new JsonParsingException(ex);
            } catch (Exception e) {
                throw new JsonParsingException(e);
            }

            viewPager.setAdapter(new LocationDetailsPagerAdapter(getFragmentManager(),
                    new Fragment[] {
                        LocationDetailsMainPageFragment.newInstance(locationData),
                        LocationDetailsCheckinPageFragment.newInstance(locationData.getCheckins())
                    }));

            viewAnimator.addView(viewPager, VIEW_PAGER_CHILD_NUMBER, LAYOUT_PARAMS);
        }
    }

    private void retryButtonAction(View view) {
        startLoadingData();
    }

    private class LocationDetailsPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments;

        LocationDetailsPagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
