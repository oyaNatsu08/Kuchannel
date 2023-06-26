package com.example.Kuchannel.entity;

import lombok.Data;

//レビューごとの返信情報を保持するクラス(ユーザー名も取得する)
@Data
public class ReviewReply {

    Integer replyId;

    Integer userId;

    String userName;

    Integer reviewId;

    String reply;

    String createDate;

    public ReviewReply(Integer replyId, Integer userId, String userName, Integer reviewId, String reply, String createDate) {
        this.replyId = replyId;
        this.userId = userId;
        this.userName = userName;
        this.reviewId = reviewId;
        this.reply = reply;
        this.createDate = createDate;
    }
}
