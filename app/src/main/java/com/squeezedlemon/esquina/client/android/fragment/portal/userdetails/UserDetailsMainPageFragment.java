package com.squeezedlemon.esquina.client.android.fragment.portal.userdetails;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.context.UserContext;
import com.squeezedlemon.esquina.client.android.data.json.Gender;
import com.squeezedlemon.esquina.client.android.data.json.UserData;
import com.squeezedlemon.esquina.client.android.data.json.UserDetails;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.UserDetailsFragment;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UserDetailsMainPageFragment extends Fragment implements CloudResultReceiver.Receiver  {

    private static final String DIALOG_LOADER_TAG = "loader";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private FragmentData fragmentData;

    private LoaderFragment loader;

    private Navigator navigator;

    /**
     *
     */
    private CloudResultReceiver receiver;

    /**
     * Create fragment with this method when you present information about logged user
     * (there isn't present button for follow/unfollow user)
     */
    public static UserDetailsMainPageFragment newInstance(UserData userData) {
        UserDetailsMainPageFragment fragment = new UserDetailsMainPageFragment();
        fragment.fragmentData = new FragmentData(userData, true, null);
        return fragment;
    }

    /**
     * Create fragment with this method when you present information about other than logged user
     * (there is present button for follow/unfollow user)
     */
    public static UserDetailsMainPageFragment newInstance(UserData userData, boolean follows) {
        UserDetailsMainPageFragment fragment = new UserDetailsMainPageFragment();
        fragment.fragmentData = new FragmentData(userData, false, follows);
        return fragment;
    }

    public UserDetailsMainPageFragment() {

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

        receiver = new CloudResultReceiver(new Handler());
        receiver.setReceiver(this);

        View v = inflater.inflate(R.layout.fragment_user_details_main_page, container, false);

        ((TextView) v.findViewById(R.id.accountNameTextView)).setText(fragmentData.getUserData().getAccountName());
        ((TextView) v.findViewById(R.id.firstNameTextView)).setText(fragmentData.getUserData().getFirstName());

        if (!fragmentData.getLoggedUserDetails()) {
            Button b = (Button) v.findViewById(R.id.followButton);
            b.setText((!fragmentData.getFollows() ? R.string.followLabel : R.string.unfollowLabel));
            b.setBackground((!fragmentData.getFollows() ? getResources().getDrawable(R.drawable.button_follow) :
                    getResources().getDrawable(R.drawable.button_unfollow)));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeFollowState(v);
                }
            });
        } else {
            v.findViewById(R.id.buttonsBar).setVisibility(View.GONE);
        }

        if (!StringUtils.hasText(fragmentData.getUserData().getMiddleName())) {
            v.findViewById(R.id.middleNameLabelTextView).setVisibility(View.GONE);
            v.findViewById(R.id.middleNameTextView).setVisibility(View.GONE);
        } else {
            ((TextView) v.findViewById(R.id.middleNameTextView)).setText(fragmentData.getUserData().getMiddleName());
        }

        ((TextView) v.findViewById(R.id.lastNameTextView)).setText(fragmentData.getUserData().getLastName());
        ((TextView) v.findViewById(R.id.genderTextView)).setText(
                fragmentData.getUserData().getGender().equals(Gender.MALE) ? R.string.genderMale : R.string.genderFemale
        );
        ((TextView) v.findViewById(R.id.birthDateTextView)).setText(dateFormat.format(fragmentData.getUserData().getBirthDate()));

        if (fragmentData.getUserData().getAboutMe() != null) {
            ((TextView) v.findViewById(R.id.aboutMeTextView)).setText(fragmentData.getUserData().getAboutMe());
        } else {
            v.findViewById(R.id.aboutMeFieldsetContainer).setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            receiver.setReceiver(null);
        }
    }

    private void changeFollowState(View v) {
        loader = new LoaderFragment();
        loader.addCancelListener(new LoaderFragment.CancelListener() {
            @Override
            public void canceled() {
                cancel();
            }
        });
        loader.show(getFragmentManager(), DIALOG_LOADER_TAG);

        HashMap<String, Object> queryParamMap = new HashMap<>();
        queryParamMap.put("username", fragmentData.getUserData().getAccountName());

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
        intent.putExtra(CloudService.RECEIVER, receiver);
        intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat("/user/logged/following/{username}"));
        intent.putExtra(CloudService.METHOD_TYPE,
                (fragmentData.getFollows() == true ? HttpMethod.DELETE.toString() : HttpMethod.POST.toString()));
        intent.putExtra(CloudService.QUERY_PARAMS, queryParamMap);
        intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
        intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        loader.dismiss();
        if (resultCode == CloudService.STATUS_CONNECTION_ERROR) {
            showAlertDialog(getString(R.string.connection_error_info));
        } else {
            Map<String, Object> responseMap = (Map<String, Object>) resultData.getSerializable(CloudService.RESPONSE_MAP);
            if ( ((HttpStatus) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_STATUS)).series().equals(HttpStatus.Series.SUCCESSFUL) ) {
                refreshUserDetailsFragment();
            } else {
                try {
                    String errorMessage = new ResponseJsonError(getActivity(), (String) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_BODY))
                            .getUserFriendlyMessage();
                    showAlertDialog(errorMessage);
                } catch (JsonParsingException e) {
                    e.printStackTrace();
                    showAlertDialog(getString(R.string.cannot_parse_response_info));
                }
            }
        }
    }

    private void cancel() {
        receiver.setReceiver(null);
        loader.dismiss();
        showAlertDialog(getString(R.string.request_canceled_text));
    }

    private void showAlertDialog(String message) {
        AlertDialogUtils.createAlertDialog(getActivity(), message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refreshUserDetailsFragment();
            }
        }).show();
    }

    private void refreshUserDetailsFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(UserDetailsFragment.BUNDLE_ACCOUNT_NAME, fragmentData.getUserData().getAccountName());
        navigator.navigate(UserDetailsMainPageFragment.this, NavigatorEnum.USER_DETAILS, bundle);
    }

    /**
     * Class stores all important information to redraw this fragment
     */
    private static class FragmentData implements Serializable {

        private UserData userData;

        private Boolean loggedUserDetails;

        private Boolean follows;

        private FragmentData(UserData userData, Boolean loggedUserDetails, Boolean follows) {
            this.userData = userData;
            this.loggedUserDetails = loggedUserDetails;
            this.follows = follows;
        }

        public UserData getUserData() {
            return userData;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }

        public Boolean getLoggedUserDetails() {
            return loggedUserDetails;
        }

        public void setLoggedUserDetails(Boolean loggedUserDetails) {
            this.loggedUserDetails = loggedUserDetails;
        }

        public Boolean getFollows() {
            return follows;
        }

        public void setFollows(Boolean follows) {
            this.follows = follows;
        }
    }

}
