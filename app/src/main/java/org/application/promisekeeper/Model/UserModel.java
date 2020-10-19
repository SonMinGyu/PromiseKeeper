package org.application.promisekeeper.Model;

public class UserModel {
    private String userUid;
    private String userEmail;
    private String userPassword;
    private String userName;
    private int userNumberCode;
    private LocationDataModel locationDataModel;
    private double userLatitude;
    private double userLongitude;

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public LocationDataModel getLocationDataModel() {
        return locationDataModel;
    }

    public void setLocationDataModel(LocationDataModel locationDataModel) {
        this.locationDataModel = locationDataModel;
    }

    public int getUserNumberCode() {
        return userNumberCode;
    }

    public void setUserNumberCode(int userNumberCode) {
        this.userNumberCode = userNumberCode;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
