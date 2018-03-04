package com.squeezedlemon.esquina.client.android.fragment.portal;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.context.UserContext;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
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

import java.util.HashMap;
import java.util.Map;

public class CheckinFragment extends Fragment implements LocationListener, CloudResultReceiver.Receiver {

    private static final String DIALOG_LOADER_TAG = "dialog";

    public static final String BUNDLE_LOCATION_ID = "locationId";

    private LocationManager locationManager;

    private boolean invalidateLocation = true;

    private Location location;

    private Navigator navigator;

    private Long locationId;

    private CloudResultReceiver receiver;

    private LoaderFragment loader;

    public static CheckinFragment newInstance(Bundle bundle) {
        CheckinFragment fragment = new CheckinFragment();
        fragment.locationId = bundle.getLong(BUNDLE_LOCATION_ID);
        return fragment;
    }

    public CheckinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            receiver.setReceiver(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, this);
        if (invalidateLocation) {
            setWaitingIndicatorVisibility(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        invalidateLocation = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        receiver = new CloudResultReceiver(new Handler());
        receiver.setReceiver(this);

        final View view = inflater.inflate(R.layout.fragment_checkin, container, false);
        view.findViewById(R.id.commentEditText).setEnabled(false);
        CheckBox commentCheckBox = (CheckBox) view.findViewById(R.id.commentEnabledCheckBox);
        commentCheckBox.setChecked(false);
        commentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.commentEditText).setEnabled(((CheckBox) v).isChecked());
            }
        });

        view.findViewById(R.id.scoreRatingBar).setEnabled(false);
        CheckBox scoreCheckBox = (CheckBox) view.findViewById(R.id.scoreEnabledCheckBox);
        scoreCheckBox.setChecked(false);
        scoreCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.scoreRatingBar).setEnabled(((CheckBox) v).isChecked());
            }
        });

        view.findViewById(R.id.checkinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!invalidateLocation) {
                    checkin();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.gpsNotReady), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        invalidateLocation = false;
        setWaitingIndicatorVisibility(false);
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        loader.dismiss();
        if (resultCode == CloudService.STATUS_CONNECTION_ERROR) {
            showAlertDialog(getString(R.string.connection_error_info));
        } else {
            Map<String, Object> responseMap = (Map<String, Object>) resultData.getSerializable(CloudService.RESPONSE_MAP);
            if ( ((HttpStatus) responseMap.get(ResponseEntityUtils.RESPONSE_MAP_STATUS)).series().equals(HttpStatus.Series.SUCCESSFUL) ) {
                backToLocationPage();
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

    private void setWaitingIndicatorVisibility(boolean visible) {
        View v = getView();
        if (v != null) {
            v.findViewById(R.id.gpsProgressBar).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            v.findViewById(R.id.gpsTextView).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void cancel() {
        receiver.setReceiver(null);
        loader.dismiss();
        showAlertDialog(getString(R.string.request_canceled_text));
    }

    private void checkin() {

        Double score = null;
        if ( ((CheckBox)getView().findViewById(R.id.scoreEnabledCheckBox)).isChecked() ) {
            score = Double.valueOf(((RatingBar) getView().findViewById(R.id.scoreRatingBar)).getRating());
        }

        String comment = null;
        if ( ((CheckBox) getView().findViewById(R.id.commentEnabledCheckBox)).isChecked() ) {
            comment = ((EditText) getView().findViewById(R.id.commentEditText)).getText().toString();
        }

        loader = new LoaderFragment();
        loader.addCancelListener(new LoaderFragment.CancelListener() {
            @Override
            public void canceled() {
                cancel();
            }
        });
        loader.show(getFragmentManager(), DIALOG_LOADER_TAG);

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("locationId", locationId);
        bodyMap.put("latitude", location.getLatitude());
        bodyMap.put("longitude", location.getLongitude());
        bodyMap.put("score", score);
        bodyMap.put("comment", comment);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), CloudService.class);
        intent.putExtra(CloudService.RECEIVER, receiver);
        intent.putExtra(CloudService.URI, Constants.APPLICATION_SERVER_URL.concat("/user/logged/checkin"));
        intent.putExtra(CloudService.METHOD_TYPE, HttpMethod.POST.toString());
        intent.putExtra(CloudService.BODY, bodyMap);
        intent.putExtra(CloudService.USERNAME, UserContext.getUsername());
        intent.putExtra(CloudService.PASSWORD, UserContext.getPassword());
        getActivity().startService(intent);
    }

    private void showAlertDialog(String message) {
        AlertDialogUtils.createAlertDialog(getActivity(), message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                backToLocationPage();
            }
        }).show();
    }

    private void backToLocationPage() {
        Bundle bundle = new Bundle();
        bundle.putLong(LocationDetailsFragment.BUNDLE_LOCATION_ID, locationId);
        navigator.navigate(this, NavigatorEnum.LOCATION_DETAILS, bundle);
    }
}
