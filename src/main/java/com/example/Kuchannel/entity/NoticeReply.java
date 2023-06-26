package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class NoticeReply {
    String replyUserName;
    String threadTitle;
    Boolean flag;
    Integer reviewId;
    String linkToReview;

    public NoticeReply(String replyUserName, String threadTitle, Boolean flag, Integer reviewId){
        this.replyUserName =replyUserName;
        this.threadTitle =threadTitle;
        this.flag =flag;
        this.reviewId =reviewId;
    }
}
