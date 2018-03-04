package com.squeezedlemon.esquina.client.android.data;

import java.math.BigDecimal;
import java.util.Date;

public class UserCheckinEntry {

    private LocationEntry locationEntry;

    private BigDecimal score;

    private String comment;

    private Date checkinDate;

    public UserCheckinEntry(LocationEntry locationEntry, BigDecimal score, String comment, Date checkinDate) {
        this.locationEntry = locationEntry;
        this.score = score;
        this.comment = comment;
        this.checkinDate = checkinDate;
    }

    public LocationEntry getLocationEntry() {
        return locationEntry;
    }

    public void setLocationEntry(LocationEntry locationEntry) {
        this.locationEntry = locationEntry;
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

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }
}
