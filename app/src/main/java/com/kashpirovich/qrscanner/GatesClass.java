package com.kashpirovich.qrscanner;

import android.os.Parcel;
import android.os.Parcelable;

public class GatesClass implements Parcelable {
    public static final Parcelable.Creator<GatesClass> CREATOR = new Parcelable.Creator<GatesClass>() {
        @Override
        public GatesClass createFromParcel(Parcel in) {
            return new GatesClass(in);
        }

        @Override
        public GatesClass[] newArray(int size) {
            return new GatesClass[size];
        }
    };
    private final String name;
    private final int id, idVenue;

    public GatesClass(int id, String name, int idVenue) {
        this.id = id;
        this.name = name;
        this.idVenue = idVenue;
    }

    protected GatesClass(Parcel in) {
        id = in.readInt();
        name = in.readString();
        idVenue = in.readInt();
    }

    public String getName() {
        return name;
    }

    public int getIdVenue() {
        return idVenue;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(idVenue);
    }
}
