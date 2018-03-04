package com.squeezedlemon.esquina.client.android.fragment.login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.json.LanguageCode;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;
import com.squeezedlemon.esquina.client.android.form.CreateAccountForm;
import com.squeezedlemon.esquina.client.android.fragment.LoaderFragment;
import com.squeezedlemon.esquina.client.android.navigator.Navigator;
import com.squeezedlemon.esquina.client.android.rest.HttpEntityBuilder;
import com.squeezedlemon.esquina.client.android.util.Constants;
import com.squeezedlemon.esquina.client.android.adapter.LanguageSpinAdapter;
import com.squeezedlemon.esquina.client.android.util.ResponseJsonError;
import com.squeezedlemon.esquina.client.android.util.StringToNullUtils;
import com.squeezedlemon.esquina.client.android.util.ViewUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateAccountFragment extends Fragment {

    public static final String ACCOUNT_URI = "/account";
    private static final String LANG_CODE_URI = "/langCode";

    private static final String LANG_CODES_PROPERTY = "langCodes";

    private static final String ACCOUNT_NAME_PROPERTY = "accountName";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String LANGUAGE_PROPERTY = "language";
    private static final String FIRST_NAME_PROPERTY = "firstName";
    private static final String MIDDLE_NAME_PROPERTY = "middleName";
    private static final String LAST_NAME_PROPERTY = "lastName";
    private static final String BIRTH_DATE_PROPERTY = "birthDate";
    private static final String GENDER_PROPERTY = "gender";
    private static final String ABOUT_ME_PROPERTY = "aboutMe";

    private static final String GENDER_MALE = "MALE";
    private static final String GENDER_FEMALE = "FEMALE";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private PreloadDataTask preloadDataTask;

    private CreateAccountForm form;

    private ViewAnimator viewAnimator;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    public CreateAccountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_animator, container, false);

        viewAnimator = (ViewAnimator) view.findViewById(R.id.rootContainer);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //Create Loader view
        viewAnimator.addView(inflater.inflate(R.layout.view_loader, viewAnimator, false), CreateAccountFragmentViewEnum.LOADER.ordinal(), layoutParams);

        //Create Create accout view
        View createAccountView = inflater.inflate(R.layout.view_create_account, viewAnimator, false);
        ((Button) createAccountView.findViewById(R.id.createAccountButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        ((EditText) createAccountView.findViewById(R.id.birthDateEditText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) v;
                final Calendar calendar = Calendar.getInstance();

                if (!editText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(dateFormat.parse(editText.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        editText.setText(dateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        viewAnimator.addView(createAccountView, CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal(), layoutParams);

        //Create account successfully created view
        viewAnimator.addView(inflater.inflate(R.layout.view_account_successfully_created, viewAnimator, false),
                CreateAccountFragmentViewEnum.ACCOUNT_CREATED.ordinal(), layoutParams);

        //Create Connection problem view
        View connectionProblemView = inflater.inflate(R.layout.view_connection_problem, viewAnimator, false);
        ((Button) connectionProblemView.findViewById(R.id.retryButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconnect();
            }
        });
        viewAnimator.addView(connectionProblemView, CreateAccountFragmentViewEnum.CONNECTION_PROBLEM.ordinal(), layoutParams);

        //Set default displayed child
        viewAnimator.setDisplayedChild(CreateAccountFragmentViewEnum.LOADER.ordinal());

        //Init form
        initForm(view);

        //Return view
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Navigator)) {
            throw new ClassCastException(activity.getClass().getName().concat(" must implement Navigator interface"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (preloadDataTask == null) {
            preloadDataTask = new PreloadDataTask();
            preloadDataTask.execute();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initForm(View view) {
        View createAccountView = viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal());

        form = new CreateAccountForm();
        form.setAccountNameEditText((EditText) createAccountView.findViewById(R.id.accountNameEditText));
        form.setPasswordEditText((EditText) createAccountView.findViewById((R.id.passwordEditText)));
        form.setRetypedPasswordEditText((EditText) createAccountView.findViewById(R.id.retypePasswordEditText));
        form.setDefaultLanguageSpinner((Spinner) createAccountView.findViewById(R.id.languageSpinner));
        form.setFirstNameEditText((EditText) createAccountView.findViewById(R.id.firstNameEditText));
        form.setMiddleNameEditText((EditText) createAccountView.findViewById(R.id.middleNameEditText));
        form.setLastNameEditText((EditText) createAccountView.findViewById(R.id.lastNameEditText));
        form.setBirthDateEditText((EditText) createAccountView.findViewById(R.id.birthDateEditText));
        form.setGenderGroup((RadioGroup) createAccountView.findViewById(R.id.genderRadioGroup));
        form.setAboutMeEditText((EditText) createAccountView.findViewById(R.id.aboutMeEditText));
    }

    private void reconnect() {
        viewAnimator.setDisplayedChild(CreateAccountFragmentViewEnum.LOADER.ordinal());
        preloadDataTask.cancel(true);
        preloadDataTask = new PreloadDataTask();
        preloadDataTask.execute();
    }

    private void createAccount() {
        ViewUtils.clearErrors(viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal()));
        if (validate()) {
            Map<String, Object> json = buildJson();
            new CreateAccountTask(json).execute();
        }
    }

    private boolean validate() {
        boolean hasErrors = false;

        if (!StringUtils.hasText(form.getAccountNameEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError(form.getAccountNameEditText(), getString(R.string.valid_account_name_required));
        }
        if (!StringUtils.hasLength(form.getPasswordEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError(form.getPasswordEditText(), getString(R.string.valid_passowrd_required));
        }
        if (!StringUtils.hasLength(form.getRetypedPasswordEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError(form.getRetypedPasswordEditText(), getString(R.string.valid_retype_pasword_required));
        }
        if (!StringUtils.hasText(form.getFirstNameEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError(form.getFirstNameEditText(), getString(R.string.valid_first_name_required));
        }
        if (!StringUtils.hasText(form.getLastNameEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError(form.getLastNameEditText(), getString(R.string.valid_last_name_required));
        }
        if (form.getGenderGroup().getCheckedRadioButtonId() == -1) {
            hasErrors = true;
            ViewUtils.appendError(((TextView) viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal())
                    .findViewById(R.id.genderAsteriskTextView)), getString(R.string.valid_gender_group_required));
        }
        if (!StringUtils.hasText(form.getBirthDateEditText().getText())) {
            hasErrors = true;
            ViewUtils.appendError((TextView) viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal())
                    .findViewById(R.id.birthDateErrorTextView), getString(R.string.valid_birth_date_required));
        }
        if (!form.getPasswordEditText().getText().toString().equals(form.getRetypedPasswordEditText().getText().toString())) {
            hasErrors = true;
            ViewUtils.appendError(form.getRetypedPasswordEditText(), getString(R.string.valid_retype_password_mismatch));
        }

        return !hasErrors;
    }

    private Map<String, Object> buildJson() {
        Map<String, Object> json = new HashMap<>();
        json.put(ACCOUNT_NAME_PROPERTY, form.getAccountNameEditText().getText().toString());
        json.put(PASSWORD_PROPERTY, form.getPasswordEditText().getText().toString());
        json.put(LANGUAGE_PROPERTY, ((LanguageCode) form.getDefaultLanguageSpinner().getSelectedItem()).getLangCode());
        json.put(FIRST_NAME_PROPERTY, form.getFirstNameEditText().getText().toString());
        json.put(MIDDLE_NAME_PROPERTY, StringToNullUtils.emptyStringToNull(form.getMiddleNameEditText().getText().toString()));
        json.put(LAST_NAME_PROPERTY, form.getLastNameEditText().getText().toString());
        json.put(BIRTH_DATE_PROPERTY, form.getBirthDateEditText().getText().toString());
        json.put(GENDER_PROPERTY, (form.getGenderGroup().getCheckedRadioButtonId() == R.id.maleRadioButton ?
                GENDER_MALE : GENDER_FEMALE));
        json.put(ABOUT_ME_PROPERTY, StringToNullUtils.emptyStringToNull(form.getAboutMeEditText().getText().toString()));

        return json;
    }

    private static enum CreateAccountFragmentViewEnum {
        LOADER,
        CREATE_ACCOUNT,
        ACCOUNT_CREATED,
        CONNECTION_PROBLEM
    }

    private class PreloadDataTask extends AsyncTask<Void, Void, Void> {

        private ResponseEntity<JsonNode> response;

        PreloadDataTask() {

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
                HttpEntity<?> requestEntity = new HttpEntityBuilder().build();
                response = rest.exchange(Constants.APPLICATION_SERVER_URL.concat(LANG_CODE_URI),
                        HttpMethod.GET, requestEntity, JsonNode.class);
            } catch (RestClientException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                //Fill language spinner
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode langCodes = response.getBody().get(LANG_CODES_PROPERTY);
                    if (langCodes.isNull()) {
                        throw new IllegalArgumentException("Property not found in JSON");
                    }

                    List<LanguageCode> languageCodes = mapper.convertValue(langCodes,
                            mapper.getTypeFactory().constructCollectionType(List.class, LanguageCode.class));

                    Spinner spinner = form.getDefaultLanguageSpinner();
                    LanguageSpinAdapter adapter = new LanguageSpinAdapter(getActivity(), android.R.layout.simple_spinner_item, 0, languageCodes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    viewAnimator.setDisplayedChild(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    viewAnimator.setDisplayedChild(CreateAccountFragmentViewEnum.CONNECTION_PROBLEM.ordinal());
                    ((TextView) viewAnimator.getCurrentView().findViewById(R.id.connectionProblemTextView)).setText(R.string.cannot_parse_response_info);
                }

            } else {
                ((ViewAnimator) getView().findViewById(R.id.rootContainer)).setDisplayedChild(CreateAccountFragmentViewEnum.CONNECTION_PROBLEM.ordinal());
                ((TextView) getView().findViewById(R.id.connectionProblemTextView)).setText(R.string.connection_error_info);
            }
        }
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Void> {

        public static final String DIALOG_LOADER_TAG = "dialog";
        private Map<String, Object> json;
        private ResponseEntity<JsonNode> response;
        private LoaderFragment loader;

        CreateAccountTask(Map<String, Object> json) {
            this.json = json;
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
        protected void onCancelled() {
            super.onCancelled();
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
                        .setBody(json)
                        .build();
                response = rest.exchange(Constants.APPLICATION_SERVER_URL.concat(ACCOUNT_URI), HttpMethod.POST, requestEntity, JsonNode.class);
            } catch (RestClientException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loader.dismiss();

            if (response == null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }

            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                viewAnimator.setDisplayedChild(CreateAccountFragmentViewEnum.ACCOUNT_CREATED.ordinal());
            } else {
                try {
                    ResponseJsonError jsonError = new ResponseJsonError(getActivity(), response.getBody());
                    if (jsonError.getValidationErrors() != null) {
                        showValidationErrors(jsonError.getValidationErrors());
                    } else {
                        Toast.makeText(getActivity(), jsonError.getUserFriendlyMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JsonParsingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.critical_error), Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void showValidationErrors(Map<String, String[]> errors) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String[]> item : errors.entrySet()) {

                stringBuilder.setLength(0);
                for (String msg : item.getValue()) {
                    stringBuilder.append(msg).append('\n');
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                switch (item.getKey()) {
                    case ACCOUNT_NAME_PROPERTY:
                        ViewUtils.appendError(form.getAccountNameEditText(), stringBuilder.toString());
                        break;

                    case PASSWORD_PROPERTY:
                        ViewUtils.appendError(form.getPasswordEditText(), stringBuilder.toString());
                        break;

                    case LANGUAGE_PROPERTY:
                        ViewUtils.appendError((TextView) viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal())
                                .findViewById(R.id.languageAsteriskTextView), stringBuilder.toString());
                        break;

                    case FIRST_NAME_PROPERTY:
                        ViewUtils.appendError(form.getFirstNameEditText(), stringBuilder.toString());
                        break;

                    case MIDDLE_NAME_PROPERTY:
                        ViewUtils.appendError(form.getMiddleNameEditText(), stringBuilder.toString());
                        break;

                    case LAST_NAME_PROPERTY:
                        ViewUtils.appendError(form.getLastNameEditText(), stringBuilder.toString());
                        break;

                    case BIRTH_DATE_PROPERTY:
                        ViewUtils.appendError((TextView) viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal())
                                .findViewById(R.id.birthDateErrorTextView), stringBuilder.toString());
                        break;

                    case GENDER_PROPERTY:
                        ViewUtils.appendError((TextView) viewAnimator.getChildAt(CreateAccountFragmentViewEnum.CREATE_ACCOUNT.ordinal())
                                .findViewById(R.id.genderAsteriskTextView), stringBuilder.toString());
                        break;

                    case ABOUT_ME_PROPERTY:
                        ViewUtils.appendError(form.getAboutMeEditText(), stringBuilder.toString());
                        break;
                }
            }
        }
    }
}
