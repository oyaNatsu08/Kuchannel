package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class FindReview {

    Integer reviewId;

    Integer threadId;

    String reviewTitle;

    String reviewContent;

    Integer userId;

    String userName;

    public FindReview() {
    }

}
