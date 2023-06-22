package com.example.Kuchannel.entity;


import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//スレッド一覧ページ用のスレッド情報。
@Data
public class CommunityThread {
    Integer id;
    Integer user_id;
    Integer community_id;
    String image_path;
    String title;
    String address;
    String sales_time;
    String genre;
    LocalDate create_date;
    Integer good_count;
    String hashtags;
    List<HashTag> hashTagList;

//スレッド一覧のページ用。スレッドの情報のエンティティ
//スレッドの情報と、いいね、ハッシュタグを取得している。

    public CommunityThread(Integer id, Integer user_id, Integer community_id, String image_path, String title, String address, String sales_time, String genre,
                            LocalDate create_date,Integer good_count,String hashTags ){
        this.id = id;
        this.user_id =user_id;
        this.community_id = community_id;
        this.image_path = image_path;
        this.title = title;
        this.address = address;
        this.sales_time = sales_time;
        this.genre = genre;
        this.create_date = create_date;
        this.good_count = good_count;
        this.hashtags = hashTags;
        this.hashTagList =new ArrayList<>();

        if(good_count == null){
            this.good_count = 0;
        }
        //取ってきたハッシュタグをリストにする処理。
        if(hashtags != null){
            String[] hashTagSplit = hashtags.split(",");
            for (var i=0; hashTagSplit.length>i;i++){
                this.hashTagList.add(new HashTag("#"+hashTagSplit[i]));
            }
        }

    }



}
