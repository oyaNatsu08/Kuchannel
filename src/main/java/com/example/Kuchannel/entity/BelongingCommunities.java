package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class BelongingCommunities {

    private int communityId;
    private Integer userId;
    private String communityName;
    private String nickName;
    private boolean flag;

    public BelongingCommunities(){

    }
}
