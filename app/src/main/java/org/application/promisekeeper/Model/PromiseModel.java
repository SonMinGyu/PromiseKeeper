package org.application.promisekeeper.Model;

import java.util.ArrayList;

public class PromiseModel {
    private ArrayList<String> memberUids;
    private String promiseTime;
    private String promiseTitle;
    private String promiseDate;
    private String promisePlace;
    private String promiseStTime;
    private double promisePlaceLatitude;
    private double promisePlaceLongitude;
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

    public double getPromisePlaceLatitude() {
        return promisePlaceLatitude;
    }

    public void setPromisePlaceLatitude(double promisePlaceLatitude) {
        this.promisePlaceLatitude = promisePlaceLatitude;
    }

    public double getPromisePlaceLongitude() {
        return promisePlaceLongitude;
    }

    public void setPromisePlaceLongitude(double promisePlaceLongitude) {
        this.promisePlaceLongitude = promisePlaceLongitude;
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
