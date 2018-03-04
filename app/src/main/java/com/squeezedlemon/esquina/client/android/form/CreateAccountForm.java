package com.squeezedlemon.esquina.client.android.form;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class CreateAccountForm {

    private EditText accountNameEditText;

    private EditText passwordEditText;

    private EditText retypedPasswordEditText;

    private Spinner defaultLanguageSpinner;

    private EditText firstNameEditText;

    private EditText middleNameEditText;

    private EditText lastNameEditText;

    private EditText birthDateEditText;

    private RadioGroup genderGroup;

    private EditText aboutMeEditText;

    public CreateAccountForm() {

    }

    public EditText getAccountNameEditText() {
        return accountNameEditText;
    }

    public void setAccountNameEditText(EditText accountNameEditText) {
        this.accountNameEditText = accountNameEditText;
    }

    public EditText getPasswordEditText() {
        return passwordEditText;
    }

    public void setPasswordEditText(EditText passwordEditText) {
        this.passwordEditText = passwordEditText;
    }

    public EditText getRetypedPasswordEditText() {
        return retypedPasswordEditText;
    }

    public void setRetypedPasswordEditText(EditText retypedPasswordEditText) {
        this.retypedPasswordEditText = retypedPasswordEditText;
    }

    public Spinner getDefaultLanguageSpinner() {
        return defaultLanguageSpinner;
    }

    public void setDefaultLanguageSpinner(Spinner defaultLanguageSpinner) {
        this.defaultLanguageSpinner = defaultLanguageSpinner;
    }

    public EditText getFirstNameEditText() {
        return firstNameEditText;
    }

    public void setFirstNameEditText(EditText firstNameEditText) {
        this.firstNameEditText = firstNameEditText;
    }

    public EditText getMiddleNameEditText() {
        return middleNameEditText;
    }

    public void setMiddleNameEditText(EditText middleNameEditText) {
        this.middleNameEditText = middleNameEditText;
    }

    public EditText getLastNameEditText() {
        return lastNameEditText;
    }

    public void setLastNameEditText(EditText lastNameEditText) {
        this.lastNameEditText = lastNameEditText;
    }

    public EditText getBirthDateEditText() {
        return birthDateEditText;
    }

    public void setBirthDateEditText(EditText birthDateEditText) {
        this.birthDateEditText = birthDateEditText;
    }

    public RadioGroup getGenderGroup() {
        return genderGroup;
    }

    public void setGenderGroup(RadioGroup genderGroup) {
        this.genderGroup = genderGroup;
    }

    public EditText getAboutMeEditText() {
        return aboutMeEditText;
    }

    public void setAboutMeEditText(EditText aboutMeEditText) {
        this.aboutMeEditText = aboutMeEditText;
    }
}
