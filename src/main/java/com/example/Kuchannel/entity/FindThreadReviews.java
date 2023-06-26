package com.example.Kuchannel.entity;
import lombok.Data;

import java.util.List;

@Data
public class FindThreadReviews {

    Integer id;

    Integer userId;

    Integer communityId;

    String title;

    String genre;

    Integer good_count;

    String image_path;

    List<FindReview> reviews;

    public FindThreadReviews(Integer threadId, Integer userId, Integer communityId, String title, String genre, Integer good_count, String image_path, List<FindReview> reviews) {
        this.id = threadId;
        this.userId = userId;
        this.communityId = communityId;
        this.title = title;
        this.genre = genre;
        this.good_count = good_count;
        this.image_path = image_path;
        this.reviews = reviews;
    }
}
