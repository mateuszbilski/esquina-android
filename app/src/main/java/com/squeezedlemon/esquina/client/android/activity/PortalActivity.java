package com.squeezedlemon.esquina.client.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.fragment.portal.ActivityFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.CheckinFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.LocationDetailsFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.NavigationDrawerFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.UserDetailsFragment;
import com.squeezedlemon.esquina.client.android.fragment.portal.SearchFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;

public class PortalActivity extends Activity implements Navigator {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void navigate(Fragment sender, NavigatorEnum nav, Bundle bundle) {

        FragmentTransaction transaction = null;
        switch (nav) {

            case USER_DETAILS:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, UserDetailsFragment.newInstance(bundle));
                transaction.commit();
                break;

            case SEARCH:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, SearchFragment.newInstance());
                transaction.commit();
                break;

            case LOCATION_DETAILS:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, LocationDetailsFragment.newInstance(bundle));
                transaction.commit();
                break;

            case ACTIVITY:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, ActivityFragment.newInstance());
                transaction.commit();
                break;

            case CHECKIN:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, CheckinFragment.newInstance(bundle));
                transaction.commit();
                break;

            default:
                throw new IllegalArgumentException("Cannot navigate to ".concat(nav.toString()));
        }
    }
}
