package com.testapp.videocallingwithsnich;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    String userId;
    String username;
    String userStatus;
    String gender;

    protected UserModel(Parcel in) {
        userId = in.readString();
        username = in.readString();
        userStatus = in.readString();
        gender = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserModel() {
    }

    public UserModel(String username) {
        this.username = username;
    }

    public UserModel(String userId, String username, String userStatus) {
        this.userId = userId;
        this.username = username;
        this.userStatus = userStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(username);
        parcel.writeString(userStatus);
        parcel.writeString(gender);
    }
}
