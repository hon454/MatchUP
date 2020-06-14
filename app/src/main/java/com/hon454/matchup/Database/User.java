package com.hon454.matchup.Database;

public class User {
    private String uid;
    private String profileUri;
    private String nickname;
    private String age;
    private String sex;
    private String jobs;
    private String residence;

    public User() {
    }

    public User(String uid, String profileUri, String nickname, String age, String sex, String job, String residence) {
        this.uid = uid;
        this.profileUri = profileUri;
        this.nickname = nickname;
        this.age = age;
        this.sex = sex;
        this.jobs = job;
        this.residence = residence;
    }

    public String getUid() {
        return uid;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getJobs() {
        return jobs;
    }

    public String getResidence() {
        return residence;
    }
}