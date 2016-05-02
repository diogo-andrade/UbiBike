package pt.ulisboa.tecnico.cmov.ubibike.objects;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Range;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by diogo on 30-04-2016.
 */
public class Trajectory implements Parcelable {
    private LatLng start;
    private LatLng end;
    private PolylineOptions line;

    public Trajectory (LatLng start){
        this.start = start;
    }

    public Trajectory (LatLng start, LatLng end,PolylineOptions line){
        this.start = start;
        this.end = end;

     /*   PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < list.size(); z++) {
            LatLng point = list.get(z);
            options.add(point);
        }*/
        this.line = line;
    }

    public LatLng getStart(){
        return this.start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }

    public PolylineOptions getLine() {
        return line;
    }

    public void setLine(PolylineOptions line) {
        this.line = line;
    }

   protected Trajectory(Parcel in) {
        start = (LatLng) in.readValue(LatLng.class.getClassLoader());
        end = (LatLng) in.readValue(LatLng.class.getClassLoader());
        line = (PolylineOptions) in.readValue(PolylineOptions.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(start);
        dest.writeValue(end);
        dest.writeValue(line);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trajectory> CREATOR = new Parcelable.Creator<Trajectory>() {
        @Override
        public Trajectory createFromParcel(Parcel in) {
            return new Trajectory(in);
        }

        @Override
        public Trajectory[] newArray(int size) {
            return new Trajectory[size];
        }
    };
}
