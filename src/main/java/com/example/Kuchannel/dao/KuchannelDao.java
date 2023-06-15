package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.CommunityRecord;
import com.example.Kuchannel.entity.UrlRecord;

public interface KuchannelDao {

    //urlが重複しないかチェック
    CommunityRecord checkUrl(String str, String name);

    //コミュニティテーブルインサート処理
    int communityInsert(String name, String url);

    //コミュニティユーザーテーブルインサート処理
    int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role);

    //コミュニティIDを元にURLを取得する
    UrlRecord getUrl(Integer id);

}
