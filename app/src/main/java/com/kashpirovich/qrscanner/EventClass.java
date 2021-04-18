package com.kashpirovich.qrscanner;

import android.os.Parcel;
import android.os.Parcelable;

public class EventClass implements Parcelable {
    public static final Parcelable.Creator<EventClass> CREATOR = new Parcelable.Creator<EventClass>() {
        @Override
        public EventClass createFromParcel(Parcel in) {
            return new EventClass(in);
        }

        @Override
        public EventClass[] newArray(int size) {
            return new EventClass[size];
        }
    };
    private final String nameFilm, date;
    private final int id, idVenue, gatesId;

    public EventClass(int id, int idVenue, String nameFilm, String date, int gatesId) {
        this.id = id;
        this.nameFilm = nameFilm;
        this.idVenue = idVenue;
        this.date = date;
        this.gatesId = gatesId;
    }

    protected EventClass(Parcel in) {
        id = in.readInt();
        date = in.readString();
        nameFilm = in.readString();
        idVenue = in.readInt();
        gatesId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(nameFilm);
        dest.writeInt(idVenue);
        dest.writeInt(gatesId);
    }

    public String getNameFilm() {
        return nameFilm;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getIdVenue() {
        return idVenue;
    }

    public int getGatesId() {
        return gatesId;
    }
}
