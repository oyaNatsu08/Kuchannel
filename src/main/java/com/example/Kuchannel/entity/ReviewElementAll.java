package com.example.Kuchannel.entity;

import java.util.List;

//レビュー一覧に表示する情報を取得するレコードクラス
//ReviewRecordとReviewImageRecordとReviewReplyRecordの情報を持っているレコードクラス
public record ReviewElementAll(Integer userId, String userName, Integer reviewId, String title, String review,
                               String createDate, List<ReviewImage> reviewImages,
                               List<ReviewReply> reviewReplies, Integer goodCount) {
}
