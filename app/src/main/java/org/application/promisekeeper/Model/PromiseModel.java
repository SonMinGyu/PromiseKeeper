package org.application.promisekeeper.Model;

import java.util.ArrayList;

public class PromiseModel {
    private ArrayList<String> memberUids;
    private String promiseTime;
    private String promiseTitle;
    private String promiseDate;
    private String promisePlace;
    private String promiseStTime;
    private double placeLatitude;
    private double placeLongitude;
    private String promiseHostUid;

    public String getPromiseHostUid() {
        return promiseHostUid;
    }

    public void setPromiseHostUid(String promiseHostUid) {
        this.promiseHostUid = promiseHostUid;
    }

    public String getPromiseStTime() {
        return promiseStTime;
    }

    public void setPromiseStTime(String promiseStTime) {
        this.promiseStTime = promiseStTime;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getPromisePlace() {
        return promisePlace;
    }

    public void setPromisePlace(String promisePlace) {
        this.promisePlace = promisePlace;
    }

    public String getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(String promiseDate) {
        this.promiseDate = promiseDate;
    }

    public ArrayList<String> getMemberUids() {
        return memberUids;
    }

    public void setMemberUids(ArrayList<String> memberUids) {
        this.memberUids = memberUids;
    }

    public String getPromiseTime() {
        return promiseTime;
    }

    public void setPromiseTime(String promiseTime) {
        this.promiseTime = promiseTime;
    }

    public String getPromiseTitle() {
        return promiseTitle;
    }

    public void setPromiseTitle(String promiseTitle) {
        this.promiseTitle = promiseTitle;
    }
}
