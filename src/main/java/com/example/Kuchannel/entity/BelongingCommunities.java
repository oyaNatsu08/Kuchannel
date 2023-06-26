package com.example.Kuchannel.entity;

import lombok.Data;


//マイページ用。コミュニティ一覧を表示する用のエンティティ
@Data
public class BelongingCommunities {

    private int communityId;
    private String communityUrl;
    private Integer userId;
    private String communityName;
    private String nickName;
    private boolean flag;

    public BelongingCommunities(){

    }
}
