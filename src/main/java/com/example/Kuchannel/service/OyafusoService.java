package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.OyafusoDao;
import com.example.Kuchannel.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OyafusoService {

    @Autowired
    private OyafusoDao oyafusoDao;

    //urlが重複しないかチェック
    public CommunityRecord checkUrl(String str, String name) {
        return oyafusoDao.checkUrl(str, name);
    }

    //コミュニティテーブルインサート処理
    public int communityInsert(String name, String url) {
        return oyafusoDao.communityInsert(name, url);
    }

    //コミュニティユーザーテーブルインサート処理
    public int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role) {
        return oyafusoDao.communityUserInsert(userId, communityId, nickName, role);
    }

    public UrlRecord getUrl(Integer id) {
        return oyafusoDao.getUrl(id);
    }

    //URLを元にコミュニティIDを取得する
    public CommunityRecord getCommunity(String url) {
        return oyafusoDao.getCommunity(url);
    }

    //コミュニティに参加しているかチェック(ユーザIDとurlで確認)
    public CommunityUserRecord checkJoin(Integer userId, String url) {
        return oyafusoDao.checkJoin(userId, url);
    }

    //コミュニティに参加しているかチェック(ユーザIDとコミュニティIDで確認)
    public CommunityUserRecord checkJoin(Integer userId, Integer communityId) {
        return oyafusoDao.checkJoin(userId, communityId);
    }

    //コミュニティに再参加する処理
    public int communityUserUpdate(Integer userId, Integer communityId, String nickName) {
        return oyafusoDao.communityUserUpdate(userId, communityId, nickName);
    }

    //ユーザーIDをもとにレビューをセレクト
    public ReviewRecord findReviews(Integer review) {
        return  oyafusoDao.findReviews(review);
    }

    //ユーザーのお知らせ一覧(返信)をセレクト
    public List<NoticeReplyRecord> userNotice(Integer userId) {
        return oyafusoDao.userNotice(userId);
    }

    //ユーザーのお知らせ一覧(問い合わせ)をセレクト
    public List<InquiryRecord> userInquiry(Integer userId) {
        return oyafusoDao.userInquiry(userId);
    }

    //ユーザーIDを元に、ユーザーを特定する
    public UserRecord findUser(Integer userId) {
        return oyafusoDao.findUser(userId);
    }

}
