package com.carvis;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Seamus on 19/04/2017.
 */

public class JourneyInfo implements Serializable {
    DateTime startTime;
    DateTime endTime;
    double startLatitude, startLongitude, endLatitude, endLongitude;


    public JourneyInfo(DateTime startTime, DateTime endTime, double startLatitude, double startLongitude, double endLatitude, double endLongitude){
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude  = endLatitude;
        this.endLongitude = endLongitude;
    }

    JourneyInfo(DateTime startTime, DateTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

//     public int describeContents() {
//         return 0;
//     }
//
//     public void writeToParcel(Parcel out, int flags) {
//         out.wr;
//         out.writeValue(startLocation);
//         out.writeValue(endLocation);
//     }
//
//     public static final Parcelable.Creator<JourneyInfo> CREATOR
//             = new Parcelable.Creator<JourneyInfo>() {
//         public JourneyInfo createFromParcel(Parcel in) {
//             return new JourneyInfo(in);
//         }
//
//         public JourneyInfo[] newArray(int size) {
//             return new JourneyInfo[size];
//         }
//     };
//
//     private JourneyInfo(Parcel in) {
////         startLocation = in.readBundle();
////     }
//     }
}
