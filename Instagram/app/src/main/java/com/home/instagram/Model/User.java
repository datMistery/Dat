package com.home.instagram.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String email;
    private String username;
    private String user_id;
    private long phone_number;

    public User() {
    }

    public User(String email, String username, String user_id, long phone_number) {
        this.email = email;
        this.username = username;
        this.user_id = user_id;
        this.phone_number = phone_number;
    }


    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        username = in.readString();
        user_id = in.readString();
        phone_number = in.readLong();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString(){
        return "User{" +
                "user_id=" + user_id +"\'" +
                ", phone_number='" + phone_number + "\'" +
                ", email='" + email + "\'" +
                ", username='" + username + "\'" +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(user_id);
        dest.writeLong(phone_number);
    }
}
