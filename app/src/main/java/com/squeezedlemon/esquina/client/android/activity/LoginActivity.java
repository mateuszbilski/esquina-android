package com.squeezedlemon.esquina.client.android.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.fragment.login.CreateAccountFragment;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
import com.squeezedlemon.esquina.client.android.fragment.login.LoginFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;

public class LoginActivity extends Activity implements Navigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, LoginFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void navigate(Fragment sender, NavigatorEnum nav, Bundle bundle) {
        switch (nav) {
            case CREATE_ACCOUNT_FRAGMENT:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, CreateAccountFragment.newInstance());
                transaction.commit();
                break;


            case PORTAL_ACTIVITY:
                Intent intent = new Intent(this, PortalActivity.class);
                startActivity(intent);
                break;

            default:
                throw new IllegalArgumentException("Cannot navigate to ".concat(nav.toString()));
        }
    }
}
