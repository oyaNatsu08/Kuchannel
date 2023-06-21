package com.example.Kuchannel.entity;

public record UserReviewList(String userName, Integer reviewId, String reviewTitle, String content,
                             String threadTitle, String communityName, String createDate) {
}
