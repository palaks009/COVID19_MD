package com.example.covapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String fname;
    private String email;
    private String userId;
    private String age;
    private String patient;

    public User(String email, String userId, String fname, String age, String patient) {
        this.email = email;
        this.userId = userId;
        this.fname = fname;
        this.age = age;
        this.patient = patient;
    }

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        userId = in.readString();
        fname = in.readString();
        age = in.readString();
        patient = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getage() {
        return age;
    }

    public void setage(String age) {
        this.age = age;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getfname() { return fname; }

    public void setfname(String fname) {
        this.fname = fname;
    }

    public String getpatient() { return patient; }

    public void setpatient(String patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", fname='" + fname + '\'' +
                ", age='" + age + '\'' +
                ", patient='" + patient + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //dest.writeString(user_id);
        dest.writeString(fname);
        dest.writeString(email);
        dest.writeString(age);
        dest.writeString(patient);
        dest.writeString(userId);
    }
}
