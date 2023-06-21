package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.KuchannelDao;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //データベースからレビュー一覧に表示する情報を全件取得
    public List<ReviewRecord> findReviewAll(Integer threadId) {
        return kuchannelDao.findReviewAll(threadId);
    }

    //データベースからレビューの画像情報を取得する
    public List<ReviewImageRecord> getReviewImages(Integer reviewId) {
        return kuchannelDao.getReviewImages(reviewId);
    }

    //データベースからレビューの返信情報を取得する
    public List<ReviewReplyRecord> getReviewReply(Integer reviewId) {
        return kuchannelDao.getReviewReply(reviewId);
    }

    //reviewsテーブルにインサート処理
    public int reviewInsert(int userId, int threadId, String title, String review) {
        return kuchannelDao.reviewInsert(userId, threadId, title, review);
    }

    //review_Imagesテーブルにインサート処理
    public int reviewImagesInsert(int reviewId, String imagePath) {
        return kuchannelDao.reviewImagesInsert(reviewId, imagePath);
    }

    //repliesテーブルにインサート処理
    public ReviewReplyRecord replyInsert(int userId, int reviewId, String content) {
        return kuchannelDao.replyInsert(userId, reviewId, content);
    }

    //レビューIDを元にreviewsテーブルから情報を取得する
    public ReviewRecord findReview(Integer reviewId) {
        return kuchannelDao.findReview(reviewId);
    }

    /*---------------------------------------------*/

    //threadテーブルにINSERTする処理
    public int threadInsert(ThreadAddForm threadAddForm) {
        return kuchannelDao.threadInsert(threadAddForm);
    }

    //コミュニティIDを元にスレッドを全件取得
    public List<ThreadRecord> communityThreads(Integer communityId) {
        return kuchannelDao.communityThreads(communityId);
    }

}
