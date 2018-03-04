package com.squeezedlemon.esquina.client.android.fragment.portal;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.adapter.ActivityListAdapter;
import com.squeezedlemon.esquina.client.android.context.UserContext;
import com.squeezedlemon.esquina.client.android.data.ActivityEntry;
import com.squeezedlemon.esquina.client.android.data.LocationEntry;
import com.squeezedlemon.esquina.client.android.data.UserCheckinEntry;
import com.squeezedlemon.esquina.client.android.data.UserEntry;
import com.squeezedlemon.esquina.client.android.data.json.ActivityData;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ActivityFragment extends Fragment implements CloudResultReceiver.Receiver {

    @IdRes
    private static final int VIEW_PAGER_ID = 10001;
    public static final int VIEW_LOADER_CHILD_NUMBER = 0;
    public static final int VIEW_CONNECTION_PROBLEM_CHILD_NUMBER = 1;
    public static final int VIEW_ACTIVITY_LIST_CHILD_NUMBER = 2;

    private static final ViewGroup.LayoutParams LAYOUT_PARAMS = new ViewGroup.LayoutParams
            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private ViewAnimator viewAnimator;

    private CloudResultReceiver receiver;

    /**
     * Reference to preloaded data from cloud
     */
    private Map<String, Object> responseMap;

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    public ActivityFragment() {
        // Required empty public constructor
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
                prepareActivityList();
                viewAnimator.setDisplayedChild(VIEW_ACTIVITY_LIST_CHILD_NUMBER);
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
                    prepareActivityList();
                    viewAnimator.setDisplayedChild(VIEW_ACTIVITY_LIST_CHILD_NUMBER);
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

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
        intent.putExtra(CloudService.RECEIVER, receiver);
        intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat("/user/logged/activity"));
        intent.putExtra(CloudService.METHOD_TYPE, HttpMethod.GET.toString());
        intent.putExtra(CloudService.BODY, (Serializable) null);
        intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
        intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
        getActivity().startService(intent);
    }

    private void prepareActivityList() throws  JsonParsingException{
        ObjectMapper mapper = new ObjectMapper();
        if (viewAnimator != null && viewAnimator.getChildAt(VIEW_ACTIVITY_LIST_CHILD_NUMBER) == null) {
            List<ActivityData> activityData = null;
            try {
                JsonNode node = mapper.readValue((String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY), JsonNode.class);
                activityData = mapper.convertValue(node.path("activity"),
                        mapper.getTypeFactory().constructCollectionType(List.class, ActivityData.class));
            } catch (IllegalArgumentException ex) {
                throw new JsonParsingException(ex);
            } catch (Exception e) {
                throw new JsonParsingException(e);
            }

            List<ActivityEntry> activityEntryList = new LinkedList<>();
            for (ActivityData item : activityData) {
                activityEntryList.add(new ActivityEntry(new UserCheckinEntry(
                        new LocationEntry(null, item.getLocation().getAddress(), item.getLocation().getId(), item.getLocation().getName()),
                        item.getScore(), item.getComment(), item.getDate()), new UserEntry(null, item.getUser().getAccountName(),
                        item.getUser().getFirstName(), item.getUser().getMiddleName(), item.getUser().getLastName())));
            }

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View activityListView = inflater.inflate(R.layout.view_activity_list, viewAnimator, false);
            ListView listView = (ListView) activityListView.findViewById(R.id.activityListView);
            listView.setEmptyView(activityListView.findViewById(R.id.activityListEmptyTextView));
            listView.setAdapter(new ActivityListAdapter(getActivity(), activityEntryList));

            viewAnimator.addView(activityListView, VIEW_ACTIVITY_LIST_CHILD_NUMBER, LAYOUT_PARAMS);
        }
    }

    private void retryButtonAction(View view) {
        startLoadingData();
    }
}
