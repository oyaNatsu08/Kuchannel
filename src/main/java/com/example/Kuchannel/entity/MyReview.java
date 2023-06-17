package com.example.Kuchannel.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReview {
    private String reviewTitle;
    private String review;
    private String threadTitle;
    private String communityName;
    private LocalDateTime createDate;

}
