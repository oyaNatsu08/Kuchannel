package com.example.Kuchannel.entity;

import lombok.Data;

@Data
//レビュー一覧で表示する情報を取得する(画像以外)
public class Review {

    Integer userId;
    String userName;
    Integer reviewId;
    String title;
    String review;
    String createDate;

    public Review(Integer userId, String userName, Integer reviewId, String title, String review, String createDate) {
        this.userId = userId;
        this.userName = userName;
        this.reviewId = reviewId;
        this.title = title;
        this.review = review;
        this.createDate = createDate;
    }
}
