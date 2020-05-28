package com.hon454.matchup.Database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.hon454.matchup.Database.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Post {

    public String uid;
    public String authorUid;
    public String authorName;
    public String title;
    public String subject;
    public Long createdDateTime;
    public Long dueDateTime;

    public String thumbnailDownloadUrl;

    public String leftModifier;
    public String leftTitle;
    public List<String> leftVoterUidList = new ArrayList<>();

    public String rightModifier;
    public String rightTitle;
    public List<String> rightVoterUidList = new ArrayList<>();

    /*
    이후에는 댓글 리스트를 들고 있어야 할 것
     */
    public Post() {
    }

    public Post(String uid, String authorUid, String authorName, String title, String leftModifier, String leftTitle,
                String rightModifier, String rightTitle, String subject, String thumbnailDownloadUrl) {
        createdDateTime = System.currentTimeMillis();

        this.uid = uid;
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.title = title;
        this.subject = subject;
        this.thumbnailDownloadUrl = thumbnailDownloadUrl;
        this.leftModifier = leftModifier;
        this.leftTitle = leftTitle;
        this.rightModifier = rightModifier;
        this.rightTitle = rightTitle;
        this.leftVoterUidList = leftVoterUidList;
        this.rightVoterUidList = rightVoterUidList;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("authorUid", authorUid);
        result.put("authorName", authorName);
        result.put("title", title);
        result.put("subject", subject);
        result.put("createdDateTime", createdDateTime);
        result.put("thumbnailDownloadUrl", thumbnailDownloadUrl);
        result.put("leftModifier", leftModifier);
        result.put("leftTitle", leftTitle);
        result.put("rightModifier", rightModifier);
        result.put("rightTitle", rightTitle);
        result.put("leftVoterUidList", leftVoterUidList);
        result.put("rightVoterUidList", rightVoterUidList);

        return result;
    }

    public String getThumbnailDownloadUrl() {
        return thumbnailDownloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }
}
