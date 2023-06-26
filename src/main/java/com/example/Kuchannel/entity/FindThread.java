package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class FindThread {

    Integer threadId;

    Integer userId;

    Integer communityId;

    String title;

    String genre;

    Integer good_count;

    String image_path;

}
