package com.example.Kuchannel.entity;

import java.util.List;

//レビュー一覧に表示する情報を取得するレコードクラス
//ReviewRecordとReviewImageRecordの情報を持っているレコードクラス
public record ReviewAndImages(Integer userId, String userName, Integer reviewId, String title, String review, String createDate, List<ReviewImageRecord> reviewImages) {
}
