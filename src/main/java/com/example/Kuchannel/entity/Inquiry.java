package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class Inquiry {
    Integer id;
    Integer inquiryUserId;
    String inquiryUserName;
    Integer communityId;
    String communityName;
    Boolean flag;

    public Inquiry(Integer id, Integer inquiryUserId, String inquiryUserName, Integer communityId, String communityName, Boolean flag) {
        this.id = id;
        this.inquiryUserId = inquiryUserId;
        this.inquiryUserName = inquiryUserName;
        this.communityId = communityId;
        this.communityName = communityName;
        this.flag = flag;
    }
}
