package com.squeezedlemon.esquina.client.android.data;

import android.graphics.Bitmap;

import com.squeezedlemon.esquina.client.android.data.json.LocationAddress;

public class LocationEntry {

    private Bitmap bitmap;
    private LocationAddress address;
    private Long id;
    private String name;

    public LocationEntry(Bitmap bitmap, LocationAddress address, Long id, String name) {
        this.bitmap = bitmap;
        this.address = address;
        this.id = id;
        this.name = name;
    }

    public String getFriendlyAddress() {
        if (address == null) {
            return null;
        } else {
            return String.format("%s, %s %s",  address.getCity(), address.getCountry(),
                    (address.getStreet() == null ? "" : address.getStreet()));
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public LocationAddress getAddress() {
        return address;
    }

    public void setAddress(LocationAddress address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
