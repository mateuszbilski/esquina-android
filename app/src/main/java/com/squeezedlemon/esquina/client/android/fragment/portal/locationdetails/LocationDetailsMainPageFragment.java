package com.squeezedlemon.esquina.client.android.fragment.portal.locationdetails;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.json.LocationData;
import com.squeezedlemon.esquina.client.android.data.json.LocationTag;
import com.squeezedlemon.esquina.client.android.fragment.portal.CheckinFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;
import com.squeezedlemon.esquina.client.android.util.Constants;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class LocationDetailsMainPageFragment extends Fragment {

    private LocationData locationData;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    private Navigator navigator;

    public static LocationDetailsMainPageFragment newInstance(LocationData locationData) {
        LocationDetailsMainPageFragment fragment = new LocationDetailsMainPageFragment();
        fragment.locationData = locationData;
        return fragment;
    }

    public LocationDetailsMainPageFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_details_main_page, container, false);

        v.findViewById(R.id.checkinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(CheckinFragment.BUNDLE_LOCATION_ID, locationData.getId());
                navigator.navigate(LocationDetailsMainPageFragment.this, NavigatorEnum.CHECKIN, bundle);
            }
        });

        ((TextView) v.findViewById(R.id.locationNameTextView)).setText(locationData.getName());
        ((RatingBar) v.findViewById(R.id.scoreRatingBar)).setRating(locationData.getScore().floatValue());
        ((TextView) v.findViewById(R.id.checkinCountTextView)).setText(locationData.getCheckinCount().toString());
        ((TextView) v.findViewById(R.id.latitudeTextView)).setText(locationData.getLatitude().toString());
        ((TextView) v.findViewById(R.id.longitudeTextView)).setText(locationData.getLongitude().toString());

        if (locationData.getOwner() == null) {
            v.findViewById(R.id.ownerLabelTextView).setVisibility(View.GONE);
            v.findViewById(R.id.ownerTextView).setVisibility(View.GONE);
        } else {
            ((TextView) v.findViewById(R.id.ownerTextView)).setText(locationData.getOwner());
        }

        if (locationData.getWebsite() == null) {
            v.findViewById(R.id.websiteLabelTextView).setVisibility(View.GONE);
            v.findViewById(R.id.websiteTextView).setVisibility(View.GONE);
        } else {
            TextView websiteTextView = (TextView) v.findViewById(R.id.websiteTextView);
            websiteTextView.setText(locationData.getWebsite());
        }

        ((TextView) v.findViewById(R.id.createdTextView)).setText(dateFormat.format(locationData.getCreatedDate()));

        if (locationData.getAddress() == null) {
            v.findViewById(R.id.addressFieldsetContainer).setVisibility(View.GONE);
        } else {
            ((TextView) v.findViewById(R.id.countryTextView)).setText(locationData.getAddress().getCountry());
            ((TextView) v.findViewById(R.id.cityTextView)).setText(locationData.getAddress().getCity());

            if (locationData.getAddress().getPostalCode() == null) {
                v.findViewById(R.id.postalCodeLabelTextView).setVisibility(View.GONE);
                v.findViewById(R.id.postalCodeTextView).setVisibility(View.GONE);
            } else {
                ((TextView) v.findViewById(R.id.postalCodeTextView)).setText(locationData.getAddress().getPostalCode());
            }

            if (locationData.getAddress().getStreet() == null) {
                v.findViewById(R.id.streetLabelTextView).setVisibility(View.GONE);
                v.findViewById(R.id.streetTextView).setVisibility(View.GONE);
            } else {
                ((TextView) v.findViewById(R.id.streetTextView)).setText(locationData.getAddress().getStreet());
            }
        }

        ((TextView) v.findViewById(R.id.descriptionTextView)).setText(locationData.getDescription());

        if (locationData.getTags() == null || locationData.getTags().isEmpty()) {
            v.findViewById(R.id.tagsFieldsetContainer).setVisibility(View.GONE);
        } else {
            LinearLayout tagsLinearLayout = (LinearLayout) v.findViewById(R.id.tagsLinearLayout);
            for (LocationTag item : locationData.getTags()) {
                View rowItem = inflater.inflate(R.layout.item_tag_list, tagsLinearLayout, false);
                ((TextView) rowItem.findViewById(R.id.tagNameTextView)).setText(item.getTranslation());
                tagsLinearLayout.addView(rowItem, Constants.MATCH_PARENT_LAYOUT_PARAMS);
            }
        }

        return v;
    }

}
