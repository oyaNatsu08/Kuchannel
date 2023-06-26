package com.example.Kuchannel.entity;

public record UserReviewList(String userName, Integer reviewId, String reviewTitle, String content,String threadId,
                             String threadTitle,Integer communityId ,String communityName,String communityUrl, String createDate) {
}

