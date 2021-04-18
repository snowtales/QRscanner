package com.kashpirovich.qrscanner;

import android.os.Parcel;
import android.os.Parcelable;

public class CinemasClass implements Parcelable {
    public static final Parcelable.Creator<CinemasClass> CREATOR = new Parcelable.Creator<CinemasClass>() {
        @Override
        public CinemasClass createFromParcel(Parcel in) {
            return new CinemasClass(in);
        }

        @Override
        public CinemasClass[] newArray(int size) {
            return new CinemasClass[size];
        }
    };
    private final String id, name, address;

    public CinemasClass(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    protected CinemasClass(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
    }
}
