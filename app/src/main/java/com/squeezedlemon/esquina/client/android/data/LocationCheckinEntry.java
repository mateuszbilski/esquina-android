package com.squeezedlemon.esquina.client.android.data;

import java.math.BigDecimal;
import java.util.Date;

public class LocationCheckinEntry {

    private UserEntry userEntry;

    private BigDecimal score;

    private String comment;

    private Date date;

    public LocationCheckinEntry(UserEntry userEntry, BigDecimal score, String comment, Date date) {
        this.userEntry = userEntry;
        this.score = score;
        this.comment = comment;
        this.date = date;
    }

    public UserEntry getUserEntry() {
        return userEntry;
    }

    public void setUserEntry(UserEntry userEntry) {
        this.userEntry = userEntry;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
