package com.example.Kuchannel.service;

import com.example.Kuchannel.dao.KuchannelDao;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import com.example.Kuchannel.entity.InformatonRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<ReviewImage> getReviewImages(Integer reviewId) {
        return kuchannelDao.getReviewImages(reviewId);
    }

    //データベースからレビューの返信情報を取得する
    public List<ReviewReply> getReviewReply(Integer reviewId) {
        return kuchannelDao.getReviewReply(reviewId);
    }

    //reviewsテーブルにインサート処理
    public int reviewInsert(int userId, int threadId, String title, String review) {
        return kuchannelDao.reviewInsert(userId, threadId, title, review);
    }

    //reviewsテーブルのレコードのupdate処理
    public int reviewUpdate(int reviewId, String title, String review) {
        return kuchannelDao.reviewUpdate(reviewId,title,review);
    }

    //review_Imagesテーブルにインサート処理
    public int reviewImagesInsert(int reviewId, String imagePath) {
        return kuchannelDao.reviewImagesInsert(reviewId, imagePath);
    }

    //repliesテーブルにインサート処理
    public ReviewReply replyInsert(int userId, int reviewId, String content) {
        return kuchannelDao.replyInsert(userId, reviewId, content);
    }

    //レビューIDを元にreviewsテーブルから情報を取得する
    public ReviewRecord findReview(Integer reviewId) {
        return kuchannelDao.findReview(reviewId);
    }

    //ユーザーのレビュー一覧に必要な情報を取得する
    public List<UserReviewList> getUserReview(Integer userId) {
        return kuchannelDao.getUserReview(userId);
    }

    //データベースからレビューのいいね件数を取得する
    public int getGoodReview(Integer reviewId) {
        return kuchannelDao.getGoodReview(reviewId);
    }

    //スレッドIDをもとに、スレッド情報を取得する
    public CommunityThread getThread(Integer threadId) {
        return kuchannelDao.getThread(threadId);
    }

    //ジャンルを取得する
    public List<GenreRecord> getGenres() {
        return kuchannelDao.getGenres();
    }

    //人気のハッシュタグを取得する
    public List<PopularHashTag> getPopularHashtags(Integer communityId) {
        return kuchannelDao.getPopularHashtags(communityId);
    }

    //ハッシュタグ全件取得
    public List<HashTag> getAllHashtags(Integer communityId){
        return kuchannelDao.getAllHashtags(communityId);
    }

    //キーワードとスレッドタイトルであいまい検索
    public List<CommunityThread> findKeyThread(Integer communityId, String[] keywords) {
        return kuchannelDao.findKeyThread(communityId, keywords);
    }

    //キーワードとレビューの本文、タイトルでスレッド情報をあいまい検索
    public List<FindThread> findKeyThreadReview(Integer communityId, String[] keywords) {
        return kuchannelDao.findKeyThreadReview(communityId, keywords);
    }

    //キーワードとレビューのレビューの本文、タイトルで、レビュー情報をあいまい検索
    public List<FindReview> findKeyReview(Integer threadId, String[] keywords) {
        return kuchannelDao.findKeyReview(threadId, keywords);
    }

    //お知らせテーブルの未読フラッグをアップデート
    public int readNotice(Integer noticeId) {
        return kuchannelDao.readNotice(noticeId);
    }

    //review_imagesテーブルをレビューIDをもとに削除
    public int deleteImages(Integer reviewId) {
        return kuchannelDao.deleteImages(reviewId);
    }

    /*---------------------------------------------*/

    //threadテーブルにINSERTする処理
    public int threadInsert(ThreadAddForm threadAddForm, Integer userId) {
        return kuchannelDao.threadInsert(threadAddForm, userId);
    }

    //コミュニティIDを元にスレッドを全件取得
    public List<CommunityThread> communityThreads(Integer communityId) {
        return kuchannelDao.communityThreads(communityId);
    }

    //お問い合わせ
    public int information(InformatonRecord informatonRecord){
        return kuchannelDao.information(informatonRecord);
    }

    /*-----------------------------------------------------*/

    //プロフィール編集
    public  ProfileEditRecord edit(String loginId,String name , String password){
        return kuchannelDao.edit(loginId, name, password);
    }

    public int goodDealThread(Integer thread_id, Integer user_id){
        return kuchannelDao.goodDealThread(thread_id,user_id);
    }

    public int goodDealReview(Integer reviewId, Integer userId){
        return kuchannelDao.goodDealReview(reviewId, userId);
    }

    public boolean deleteThread(Integer thread_id){
        return kuchannelDao.deleteThread(thread_id);
    }
    public boolean forcedDeleteThread(Integer thread_id){return kuchannelDao.forcedDeleteThread(thread_id);}

    public int threadUpdate(ThreadAddForm inputData,Integer thread_id){return kuchannelDao.threadUpdate(inputData,thread_id);}

/*-----------------------------------------------------------------------------*/
    //レビュー削除
    public int reviewDelete(Integer reviewId) {
        return kuchannelDao.reviewDelete(reviewId);
    }

/*-----------------------------------------------------------------------------*/

    public int goodDeal(Integer thread_id, Integer user_id){
        return kuchannelDao.goodDeal(thread_id,user_id);
    }

    //コミュニティメンバー表示用。所属コミュニティメンバーを返す
    public List<AccountInformation> getCommunityMember(Integer communityId){return kuchannelDao.getCommunityMember(communityId);}

    public AccountInformation getAccountInfo(Integer user_id, Integer community_id){return kuchannelDao.getAccountInfo(user_id,community_id);}

    //getAccountInfoのニックネームがある場合は、ニックネームで取得するバージョン
    public AccountInformation getAccountInfoNick(Integer userId, Integer communityId) {
        return kuchannelDao.getAccountInfoNick(userId, communityId);
    }

    public int memberSetting(List<AccountInformation> updateInfo, Integer communityId){return kuchannelDao.memberSetting(updateInfo,communityId);}

    public int updateCommunityName(Integer communityId, String newCommunityName){return kuchannelDao.updateCommunityName(communityId,newCommunityName);}

    public int deleteCommunity(Integer communityId){return kuchannelDao.deleteCommunity(communityId);}

    public int IntegrateThreads(ThreadAddForm threadInfo,Integer userId){return kuchannelDao.IntegrateThreads(threadInfo,userId);}

}
