package com.squeezedlemon.esquina.client.android.data;

public class ActivityEntry {

    private UserCheckinEntry userCheckinEntry;

    private UserEntry userEntry;

    public ActivityEntry(UserCheckinEntry userCheckinEntry, UserEntry userEntry) {
        this.userCheckinEntry = userCheckinEntry;
        this.userEntry = userEntry;
    }

    public UserCheckinEntry getUserCheckinEntry() {
        return userCheckinEntry;
    }

    public void setUserCheckinEntry(UserCheckinEntry userCheckinEntry) {
        this.userCheckinEntry = userCheckinEntry;
    }

    public UserEntry getUserEntry() {
        return userEntry;
    }

    public void setUserEntry(UserEntry userEntry) {
        this.userEntry = userEntry;
    }
}
