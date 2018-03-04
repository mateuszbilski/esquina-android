package com.squeezedlemon.esquina.client.android.data;

import android.graphics.Bitmap;

public class UserEntry {

    private Bitmap bitmap;
    private String accountName;
    private String firstName;
    private String middleName;
    private String lastName;

    public UserEntry(Bitmap bitmap, String accountName, String firstName, String middleName, String lastName) {
        this.bitmap = bitmap;
        this.accountName = accountName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getUserDescription() {
        if (middleName == null) {
            return String.format("%s %s (%s)", firstName, lastName, accountName);
        } else {
            return String.format("%s %s %s (%s)", firstName, middleName, lastName, accountName);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
