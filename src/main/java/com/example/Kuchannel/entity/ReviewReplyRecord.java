package com.example.Kuchannel.entity;

//レビューごとの返信情報を保持するクラス(ユーザー名も取得する)
public record ReviewReplyRecord(Integer replyId, Integer userId, String userName, Integer reviewId, String reply, String createDate) {
}
