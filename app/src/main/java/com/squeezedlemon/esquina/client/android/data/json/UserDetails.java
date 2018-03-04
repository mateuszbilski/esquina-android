package com.squeezedlemon.esquina.client.android.data.json;


import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.io.Serializable;
import java.util.List;

/**
 * Class map JSON response from server about user details
 */
public class UserDetails implements Serializable {

    @JsonUnwrapped
    private UserData userData;

    private List<UserData> followers;

    private List<UserData> following;

    private List<UserCheckin> checkins;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public List<UserData> getFollowers() {
        return followers;
    }

    public void setFollowers(List<UserData> followers) {
        this.followers = followers;
    }

    public List<UserData> getFollowing() {
        return following;
    }

    public void setFollowing(List<UserData> following) {
        this.following = following;
    }

    public List<UserCheckin> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<UserCheckin> checkins) {
        this.checkins = checkins;
    }
}
