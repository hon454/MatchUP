package com.hon454.matchup.Database;

import java.util.Date;

public class Comment {
    public String uid;
    public String author;
    public String text;

    public Comment () {
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }
}
