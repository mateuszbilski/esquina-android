package com.squeezedlemon.esquina.client.android.fragment.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.navigator.NavigatorEnum;
import com.squeezedlemon.esquina.client.android.rest.HttpEntityBuilder;
import com.squeezedlemon.esquina.client.android.util.Constants;
import com.squeezedlemon.esquina.client.android.util.ResponseJsonError;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class LoginFragment extends Fragment {

    public static final String MEMBER_GREETING_URI = "/greeting/member";

    private Navigator navigator;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {

    }

    public void login(View button) {
        String accountName =  ((EditText) getView().findViewById(R.id.accountNameEditText)).getText().toString().trim();
        String password = ((EditText) getView().findViewById(R.id.passwordEditText)).getText().toString();
        if (accountName.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.enter_auth_data), Toast.LENGTH_SHORT).show();
        } else {
            new NetworkTask(accountName, password).execute();
        }
    }

    public void navigateToCreateAccountFragment() {
        navigator.navigate(this, NavigatorEnum.CREATE_ACCOUNT_FRAGMENT, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((Button) view.findViewById(R.id.loginButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        ((TextView) view.findViewById(R.id.createAccountTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreateAccountFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Navigator)) {
            throw new ClassCastException(activity.getClass().getName().concat(" must implement Navigator interface"));
        } else {
            navigator = (Navigator) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LoginFragment", "on Pause called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LoginFragment", "on Resume called");
    }

    private class NetworkTask extends AsyncTask<Void, Void, Void> {

        public static final String DIALOG_LOADER_TAG = "dialog";
        private String accountName;
        private String password;
        private ResponseEntity<JsonNode> response;
        private LoaderFragment loader;

        NetworkTask(String accountName, String password) {
            this.accountName = accountName;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader = new LoaderFragment();
            loader.addCancelListener(new LoaderFragment.CancelListener() {
                @Override
                public void canceled() {
                    cancel(true);
                }
            });
            loader.show(getFragmentManager(), DIALOG_LOADER_TAG);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                RestTemplate rest = new RestTemplate();
                rest.setErrorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }
                });

                HttpEntity<?> requestEntity = new HttpEntityBuilder()
                        .setAuthorization(accountName, password)
                        .build();
                response = rest.exchange(Constants.APPLICATION_SERVER_URL.concat(MEMBER_GREETING_URI), HttpMethod.GET, requestEntity, JsonNode.class);
            } catch (RestClientException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loader.dismiss();

            if (response != null) {
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    navigator.navigate(LoginFragment.this, NavigatorEnum.PORTAL_ACTIVITY, null);
                } else {
                    try {
                        ResponseJsonError jsonError = new ResponseJsonError(getActivity(), response.getBody());
                        Toast.makeText(getActivity(), jsonError.getUserFriendlyMessage(), Toast.LENGTH_SHORT).show();
                    } catch (JsonParsingException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.critical_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
