package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class BelongingCommunities {

    private int communityId;
    private String communityName;
    private String nickName;
    private boolean flag;

    public BelongingCommunities(){

    }
}
