package com.hon454.matchup.Database;

public class User {
    public String uri;
    public String nickname;
    public String age;
    public String sex;
    public String jobs;
    public String residence;

    public User() {
    }

    public User(String uri, String nickname, String age, String sex, String job, String residence) {
        this.uri = uri;
        this.nickname = nickname;
        this.age = age;
        this.sex = sex;
        this.jobs = job;
        this.residence = residence;
    }
}