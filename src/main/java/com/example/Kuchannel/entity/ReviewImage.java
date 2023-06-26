package com.example.Kuchannel.entity;

import lombok.Data;

//レビューの画像情報を取得する
@Data
public class ReviewImage {

    Integer reviewId;

    String imagePath;

    public ReviewImage(Integer reviewId, String imagePath) {
        this.reviewId = reviewId;
        this.imagePath = imagePath;
    }
}
