package com.husph.mobilecomputing.models;

public class UserProfile {
    private String username;
    private String phoneNumber;
    private String province;
    private String birthDate;
    private String birthTime;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        if (username == null) return "Guest";
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String interests;

    public UserProfile() {
    }

    public UserProfile(
            String username,
            String phoneNumber,
            String province,
            String birthDate,
            String birthTime,
            String interests,
            String gender
    ) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.province = phoneNumber;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.gender = gender;
        this.interests = interests;
    }


}
