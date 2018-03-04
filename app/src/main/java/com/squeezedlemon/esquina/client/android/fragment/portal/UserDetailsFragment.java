package com.squeezedlemon.esquina.client.android.fragment.portal;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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
import com.squeezedlemon.esquina.client.android.data.json.UserData;
import com.squeezedlemon.esquina.client.android.data.json.UserDetails;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.portal.userdetails.UserDetailsCheckinPageFragment;
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


public class UserDetailsFragment extends Fragment implements CloudResultReceiver.Receiver {

    public static final String BUNDLE_ACCOUNT_NAME = "accountName";

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
     * Reference to account name acutally present
     */
    private String accountName;

    /**
     * Reference to preloaded data from cloud
     */
    private Map<String, Object> responseMap;

    /**
     * Points to actually selected page in ViewPager.
     * -1 -> no information
     */
    private int  selectedPage = -1;

    /**
     * Receives result of executed service
     */
    private CloudResultReceiver receiver;


    /**
     * On fragment creation, pass Bundle object with entry:
     * (accountName) -> user's account name, that you want to present
     */
    public static UserDetailsFragment newInstance(Bundle bundle) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.accountName = bundle.getString(BUNDLE_ACCOUNT_NAME);
        return fragment;
    }

    public UserDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         * Init View
         */
        View view = inflater.inflate(R.layout.fragment_view_animator, container, false);

        viewAnimator = (ViewAnimator) view.findViewById(R.id.rootContainer);
        viewAnimator.addView(inflater.inflate(R.layout.view_loader, viewAnimator, false), VIEW_LOADER_CHILD_NUMBER, LAYOUT_PARAMS);

        View connectionProblemView = inflater.inflate(R.layout.view_connection_problem, viewAnimator, false);
        connectionProblemView.findViewById(R.id.retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryButtonAction(v);
            }
        });
        viewAnimator.addView(connectionProblemView, VIEW_CONNECTION_PROBLEM_CHILD_NUMBER, LAYOUT_PARAMS);

        viewPager = new ViewPager(getActivity());
        viewPager.setId(VIEW_PAGER_ID);

        /**
         * Init data receiver
         */
        receiver = new CloudResultReceiver(new Handler());
        receiver.setReceiver(this);


        /**
         * Init view pager
         */
        if (responseMap != null) {
            try {
                prepareViewPager();

                if (selectedPage != -1) {
                    viewPager.setCurrentItem(selectedPage);
                }
                viewAnimator.setDisplayedChild(VIEW_PAGER_CHILD_NUMBER);
            } catch (JsonParsingException ex) {
                ex.printStackTrace();
                viewAnimator.setDisplayedChild(VIEW_CONNECTION_PROBLEM_CHILD_NUMBER);
                ((TextView) viewAnimator.getCurrentView().findViewById(R.id.connectionProblemTextView))
                        .setText(getString(R.string.critical_application_error));
            }
        } else {
            startLoadingData();
        }

        return view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            receiver.setReceiver(null);
        }
    }

    private void startLoadingData() {
        viewAnimator.setDisplayedChild(VIEW_LOADER_CHILD_NUMBER);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
        intent.putExtra(CloudService.RECEIVER, receiver);
        intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat("/user/").concat(accountName));
        intent.putExtra(CloudService.METHOD_TYPE, HttpMethod.GET.toString());
        intent.putExtra(CloudService.BODY, (Serializable) null);
        intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
        intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
        getActivity().startService(intent);
    }

    private void prepareViewPager() throws JsonParsingException {
        ObjectMapper mapper = new ObjectMapper();
        if (viewAnimator != null && viewAnimator.getChildAt(VIEW_PAGER_CHILD_NUMBER) == null) {
            UserDetails userDetails = null;
            try {
                userDetails = mapper.readValue((String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY), UserDetails.class);
            } catch (IllegalArgumentException ex) {
                throw new JsonParsingException(ex);
            } catch (Exception e) {
                throw new JsonParsingException(e);
            }

            UserDetailsMainPageFragment userDetailsMainPageFragment = null;

            if (userDetails.getUserData().getAccountName().equals(UserContext.getUsername())) {
                userDetailsMainPageFragment = UserDetailsMainPageFragment.newInstance(userDetails.getUserData());
            } else {
                boolean follows = false;
                for (UserData item : userDetails.getFollowers()) {
                    if (item.getAccountName().equals(UserContext.getUsername())) {
                        follows = true;
                        break;
                    }
                }
                userDetailsMainPageFragment = UserDetailsMainPageFragment.newInstance(userDetails.getUserData(), follows);
            }

            viewPager.setAdapter(new UserDetailsPagerAdapter(getFragmentManager(),
                    new Fragment[] {
                            userDetailsMainPageFragment,
                            UserDetailsListPageFragment.newInstance(userDetails.getFollowing(), getString(R.string.followingListEmptyText)),
                            UserDetailsListPageFragment.newInstance(userDetails.getFollowers(), getString(R.string.followersListEmptyText)),
                            UserDetailsCheckinPageFragment.newInstance(userDetails.getCheckins())
                    }));
            viewAnimator.addView(viewPager, VIEW_PAGER_CHILD_NUMBER, LAYOUT_PARAMS);
        }
    }

    private void retryButtonAction(View view) {
        startLoadingData();
    }

    private class UserDetailsPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments;

        UserDetailsPagerAdapter(FragmentManager fm, Fragment[] fragments) {
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
