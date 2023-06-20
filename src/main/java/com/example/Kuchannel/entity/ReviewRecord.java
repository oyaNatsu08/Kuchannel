package com.example.Kuchannel.entity;

//レビュー一覧で表示する情報を取得する(画像以外)
public record ReviewRecord(Integer userId, String userName, Integer reviewId, String title, String review, String createDate) {
}
