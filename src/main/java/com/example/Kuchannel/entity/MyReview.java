package com.example.Kuchannel.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

//マイページ用。レビュー一覧を表示する用のエンティティ
@Data
public class MyReview {
    private Integer reviewId;
    private String reviewTitle;
    private String review;
    private String threadTitle;
    private String communityName;
    private LocalDate createDate;
    private Integer communityId;
    private String communityUrl;
    private Integer threadId;

}
