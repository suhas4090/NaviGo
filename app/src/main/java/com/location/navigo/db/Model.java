package com.location.navigo.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Model implements Parcelable {

    private GeoPoint geoPoint;

    public Model(GeoPoint point) {
        geoPoint = point;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(geoPoint.getLatitude());
        out.writeDouble(geoPoint.getLongitude());
    }

    public static final Parcelable.Creator<Model> CREATOR
            = new Parcelable.Creator<Model>() {
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    private Model(Parcel in) {
        Double lat = in.readDouble();
        Double lon = in.readDouble();
        geoPoint = new GeoPoint(lat, lon);
    }
}
