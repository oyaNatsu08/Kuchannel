package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.KuchannelDao;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class KuchannelService {

    @Autowired
    private KuchannelDao kuchannelDao;

    //ログイン
    public UserRecord Login(String loginId, String password) {
        return kuchannelDao.Login(loginId, password);
    }

    //プロフィール画面
    public ProfileRecord detail(String loginId) {
        return kuchannelDao.detail(loginId);
    }

    //アカウント新規作成
    public CreateRecord create(String loginId, String password, String name, String image_path) {
        return kuchannelDao.create(loginId,password,name,image_path);
    }

    //プロフィール編集
//    public  ProfileEditRecord edit(int id,String name , String password){
//        return kuchannelDao.edit(name,password);
//    }

    /*--------------------------------------*/

    //マイページ用。データベースから所属しているコミュニティを取得
    public List<BelongingCommunities> getBelongingCommunities(Integer userId){return kuchannelDao.getBelongingCommunities(userId);}

    //マイページ用。退会処理。
    public int withdrawal(Integer userId,Integer communityId){return kuchannelDao.withdrawal(userId,communityId);}

    //マイページ用。ニックネームの変更
    public int updateNickName(BelongingCommunities updateInfo){
        return  kuchannelDao.updateNickName(updateInfo);
    }

    //マイページで自分の立てたスレッドを表示する用
    public List<MyThread> getMyThreads(Integer userId){return kuchannelDao.getMyThreads(userId);}


    //マイページで自分のレビューを表示する用
    public List<MyReview> getMyReviews(Integer userId){return kuchannelDao.getMyReviews(userId);}

    /*---------------------------------------*/

    //urlが重複しないかチェック
    public CommunityRecord checkUrl(String str, String name) {
        return kuchannelDao.checkUrl(str, name);
    }

    //コミュニティテーブルインサート処理
    public int communityInsert(String name, String url) {
        return kuchannelDao.communityInsert(name, url);
    }

    //コミュニティユーザーテーブルインサート処理
    public int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role) {
        return kuchannelDao.communityUserInsert(userId, communityId, nickName, role);
    }

    public UrlRecord getUrl(Integer id) {
        return kuchannelDao.getUrl(id);
    }

    //URLを元にコミュニティIDを取得する
    public CommunityRecord getCommunity(String url) {
        return kuchannelDao.getCommunity(url);
    }

    //コミュニティに参加しているかチェック(ユーザIDとurlで確認)
    public CommunityUserRecord checkJoin(Integer userId, String url) {
        return kuchannelDao.checkJoin(userId, url);
    }

    //コミュニティに参加しているかチェック(ユーザIDとコミュニティIDで確認)
    public CommunityUserRecord checkJoin(Integer userId, Integer communityId) {
        return kuchannelDao.checkJoin(userId, communityId);
    }

    //コミュニティに再参加する処理
    public int communityUserUpdate(Integer userId, Integer communityId, String nickName) {
        return kuchannelDao.communityUserUpdate(userId, communityId, nickName);
    }

    //ユーザーIDをもとにレビューをセレクト
    public ReviewRecord findReviews(Integer reviewId) {
        return  kuchannelDao.findReviews(reviewId);
    }

    //ユーザーのお知らせ一覧(返信)をセレクト
    public List<NoticeReplyRecord> userNotice(Integer userId) {
        return kuchannelDao.userNotice(userId);
    }

    //ユーザーのお知らせ一覧(問い合わせ)をセレクト
    public List<InquiryRecord> userInquiry(Integer userId) {
        return kuchannelDao.userInquiry(userId);
    }

    //ユーザーIDを元に、ユーザーを特定する
    public UserRecord findUser(Integer userId) {
        return kuchannelDao.findUser(userId);
    }

    //コミュニティIDを元にコミュニティを特定する
    public CommunityRecord findCommunity(Integer communityId) {
        return kuchannelDao.findCommunity(communityId);
    }

    //お知らせ詳細情報を取得する
    public InquiryDetailRecord findInquiry(Integer inquiryId) {
        return kuchannelDao.findInquiry(inquiryId);
    }

    /*---------------------------------------------*/

    //threadテーブルにINSERTする処理
    public int threadInsert(ThreadAddForm threadAddForm,Integer userId) {
        return kuchannelDao.threadInsert(threadAddForm,userId);
    }

    //コミュニティIDを元にスレッドを全件取得
    public List<CommunityThread> communityThreads(Integer communityId) {
        return kuchannelDao.communityThreads(communityId);
    }

    public int goodDeal(Integer thread_id, Integer user_id){
        return kuchannelDao.goodDeal(thread_id,user_id);
    }

    public boolean deleteThread(Integer thread_id){
        return kuchannelDao.deleteThread(thread_id);
    }

    public int threadUpdate(ThreadAddForm inputData,Integer thread_id){return kuchannelDao.threadUpdate(inputData,thread_id);}

    //コミュニティメンバー表示用。所属コミュニティメンバーを返す
    public List<AccountInformation> getCommunityMember(Integer communityId){return kuchannelDao.getCommunityMember(communityId);}

    public AccountInformation getAccountInfo(Integer user_id, Integer community_id){return kuchannelDao.getAccountInfo(user_id,community_id);}

    public int memberSetting(List<AccountInformation> updateInfo, Integer communityId){return kuchannelDao.memberSetting(updateInfo,communityId);}

    public int deleteCommunity(Integer communityId){return kuchannelDao.deleteCommunity(communityId);}

    public int IntegrateThreads(ThreadAddForm threadInfo,Integer userId){return kuchannelDao.IntegrateThreads(threadInfo,userId);}

}
