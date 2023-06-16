package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.*;

import java.util.List;

public interface KuchannelDao {

    //urlが重複しないかチェック
    CommunityRecord checkUrl(String str, String name);

    //コミュニティテーブルインサート処理
    int communityInsert(String name, String url);

    //コミュニティユーザーテーブルインサート処理
    int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role);

    //コミュニティIDを元にURLを取得する
    UrlRecord getUrl(Integer id);

    //URLを元にコミュニティIDを取得する
    CommunityRecord getCommunity(String url);

    //コミュニティに参加しているかチェック
    CommunityUserRecord checkJoin(Integer userId, String url);

    //レビューIDをもとにレビューをセレクト
    ReviewRecord findReviews(Integer reviewId);

    //ユーザーのお知らせ一覧をセレクト
    List<NoticeReplyRecord> userNotice(Integer userId);
    List<InquiryRecord> userInquiry(Integer userId);

    //ユーザーを特定する
    UserRecord findUser(Integer userId);

}
