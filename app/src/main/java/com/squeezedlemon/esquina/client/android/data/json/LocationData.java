package com.squeezedlemon.esquina.client.android.data.json;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class LocationData implements Serializable {

    private Long id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal score;
    private Long checkinCount;
    private Date createdDate;
    private String owner;
    private String website;
    private String icon;
    private String backgroundImage;
    private LocationAddress address;
    private List<LocationTag> tags;
    private List<LocationCheckin> checkins;

    /* Localized fields */
    private String name;
    private String description;
    private String language;

    public LocationData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Long getCheckinCount() {
        return checkinCount;
    }

    public void setCheckinCount(Long checkinCount) {
        this.checkinCount = checkinCount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public LocationAddress getAddress() {
        return address;
    }

    public void setAddress(LocationAddress address) {
        this.address = address;
    }

    public List<LocationTag> getTags() {
        return tags;
    }

    public void setTags(List<LocationTag> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<LocationCheckin> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<LocationCheckin> checkins) {
        this.checkins = checkins;
    }
}
