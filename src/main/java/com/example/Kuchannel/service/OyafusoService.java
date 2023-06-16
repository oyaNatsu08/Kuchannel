package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.KuchannelDao;
import com.example.Kuchannel.entity.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OyafusoService implements KuchannelService {

    @Autowired
    private KuchannelDao oyafusoDao;

    //urlが重複しないかチェック
    @Override
    public CommunityRecord checkUrl(String str, String name) {
        return oyafusoDao.checkUrl(str, name);
    }

    //コミュニティテーブルインサート処理
    @Override
    public int communityInsert(String name, String url) {
        return oyafusoDao.communityInsert(name, url);
    }

    //コミュニティユーザーテーブルインサート処理
    @Override
    public int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role) {
        return oyafusoDao.communityUserInsert(userId, communityId, nickName, role);
    }

    @Override
    public UrlRecord getUrl(Integer id) {
        return oyafusoDao.getUrl(id);
    }

    //URLを元にコミュニティIDを取得する
    @Override
    public CommunityRecord getCommunity(String url) {
        return oyafusoDao.getCommunity(url);
    }

    //コミュニティに参加しているかチェック
    @Override
    public CommunityUserRecord checkJoin(Integer userId, String url) {
        return oyafusoDao.checkJoin(userId, url);
    }

    //ユーザーIDをもとにレビューをセレクト
    @Override
    public ReviewRecord findReviews(Integer review) {
        return  oyafusoDao.findReviews(review);
    }

    //ユーザーのお知らせ一覧(返信)をセレクト
    @Override
    public List<NoticeReplyRecord> userNotice(Integer userId) {
        return oyafusoDao.userNotice(userId);
    }

    //ユーザーのお知らせ一覧(問い合わせ)をセレクト
    @Override
    public List<InquiryRecord> userInquiry(Integer userId) {
        return oyafusoDao.userInquiry(userId);
    }

    //ユーザーIDを元に、ユーザーを特定する
    @Override
    public UserRecord findUser(Integer userId) {
        return oyafusoDao.findUser(userId);
    }

}
