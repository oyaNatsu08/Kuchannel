package com.example.Kuchannel.entity;

import lombok.Data;

//スレッド一覧のページ用。ハッシュタグを取得するためのエンティティ
//このクラスのリストをCommunityThreadクラスのフィールドとして持たせる予定。

@Data
public class HashTag {
    Integer id;
    String tag_name;


    public HashTag(String tag_name){
        this.tag_name = tag_name;
    }
}


