package com.squeezedlemon.esquina.client.android.data.json;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserCheckin implements Serializable {

    private Long id;
    private BigDecimal score;
    private Date date;
    private String comment;
    private LocationData location;

    public UserCheckin() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }
}
