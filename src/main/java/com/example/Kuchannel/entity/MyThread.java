package com.example.Kuchannel.entity;


import lombok.Data;


//マイページ用。スレッド一覧を表示する用のエンティティ

@Data
public class MyThread {
    private Integer threadId;
    private String threadTitle;
    private Integer communityId;
    private String communityName;
    private String communityUrl;

    public MyThread(){

    }

}
