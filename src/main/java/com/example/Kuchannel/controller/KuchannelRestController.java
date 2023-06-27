package com.example.Kuchannel.controller;

import com.example.Kuchannel.KuchannelApplication;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import com.example.Kuchannel.form.ReviewUpdateForm;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class KuchannelRestController {

    @Autowired
    private KuchannelService kuchannelService;

    @Autowired
    private HttpSession session;

    //所属しているコミュニティを取得。（マイページ用）
    @GetMapping("/getBelongingCommunities")
    public List<BelongingCommunities> getBelongingCommunities(){
        //System.out.println("ゲットコミュニティ");

        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        List<BelongingCommunities> communityList = kuchannelService.getBelongingCommunities(user.id());
        return communityList;
    }

    //マイページで任意のコミュにティから退会する用の処理。
    @PostMapping(value = "/withdrawal", consumes = "text/plain;charset=UTF-8")
    public int withdrawal(@RequestBody String getCommunityId){
        var communityId =Integer.parseInt(getCommunityId);
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        int result = kuchannelService.withdrawal(user.id(), communityId);
        return result;
    }

    //    マイページ用の、ニックネームを変更する処理
    @PostMapping("/updateNickName")
    public int updateNickName(@RequestBody BelongingCommunities updateInfo){
        int result = kuchannelService.updateNickName(updateInfo);
        return result;
    }

    //マイページ用で、スレッドを取得する
    @GetMapping("/getMyThreads")
    public List<MyThread> getMyThread(){
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している→修正済み
        UserRecord user = (UserRecord) session.getAttribute("user");
        List<MyThread> result = kuchannelService.getMyThreads(user.id());
        return result;
    }

    //マイページ用で、レビューを取得する
    @GetMapping("/getMyReviews")
    public List<MyReview> getMyReviews(){
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        List<MyReview> result = kuchannelService.getMyReviews(user.id());
        return result;
    }

//    スレッド一覧ページ用。スレッドの情報を取得
    @GetMapping("getThreads/{communityId}")
    public List<CommunityThread> getThreads(@PathVariable("communityId")Integer communityId){
//        コミュニティidを渡すようにする（今は1で固定）->修正
        var threads = kuchannelService.communityThreads(communityId);
        return threads;
    }

    @GetMapping("/goodDeal/{id}")
    public int GoodDeal(@PathVariable("id")Integer thread_id){
//        セッションからuser_idをもらう一旦1で固定
//        var user = (UserRecord)session.getAttribute("user");
        return kuchannelService.goodDeal(thread_id,1);
    }

    @GetMapping("/goodDeal/review/{reviewId}")
    public int GoodReviewDeal(@PathVariable("reviewId") Integer reviewId) {
        var user = (UserRecord)session.getAttribute("user");

        return kuchannelService.goodDealReview(reviewId, user.id());
    }

    //スレッド削除用
    @DeleteMapping("/deleteThread/{threadId}")
    public boolean deleteThread(@PathVariable("threadId")Integer thread_id){
        return kuchannelService.deleteThread(thread_id);
    }

    //スレッド削除（一般化管理者かの判断も追加版）
    @DeleteMapping("/deleteThread/{threadId}/{role}")
    public boolean deleteThread2(@PathVariable("threadId")Integer thread_id,@PathVariable("role")Integer role){
        System.out.println("スレid"+thread_id);
        System.out.println("ロール"+role);
        if(role == 2){
            System.out.println("2やねん");
            return kuchannelService.forcedDeleteThread(thread_id);
        }
        return kuchannelService.deleteThread(thread_id);
    }

    //セッション情報から、ユーザーidとroleを持った情報を返す。
    //getThreadと同じで、コミュニティidを渡すようにする。今は１で固定。
    @GetMapping("/getSessionInfo/{communityId}")
    public AccountInformation getSessionInfo(@PathVariable("communityId")Integer communityId){

        var user = (UserRecord)session.getAttribute("user");
        var accountInfo = kuchannelService.getAccountInfo(user.id(),communityId);

        return accountInfo;
    }

    //スレッド作成処理
    @PostMapping("/thread-add")
    public int addThread(@RequestParam("threadName") String threadName,
                         @RequestParam("furigana") String furigana,
                         @RequestParam("address") String address,
                         @RequestParam("salesTime") String salesTime,
                         @RequestParam("genre") String genre,
                         @RequestParam("hashtag") String hashtag,
                         @RequestParam("communityId") Integer communityId,
                         @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {    //@RequestBody ThreadAddForm inputData
        var user = (UserRecord)session.getAttribute("user");

        String encode = null;

        if (image != null) {
            byte[] bytes = image.getBytes();
            encode = Base64.getEncoder().encodeToString(bytes);
        }

        //System.out.println("ハッシュタグ" + hashtag);

        if ("null".equals(hashtag)) {
            hashtag = null;
            //System.out.println("ハッシュタグ" + hashtag);
        }

        //threadsテーブルにINSERT処理
        var result = kuchannelService.threadInsert(new ThreadAddForm(threadName, furigana, address, salesTime, genre, hashtag, communityId, encode),user.id());
        return result;
    }

    //スレッド情報編集
    @PutMapping("/updateThread/{id}")
    public int threadUpdate(@RequestParam("threadName") String threadName,
                            @RequestParam("furigana") String furigana,
                            @RequestParam("address") String address,
                            @RequestParam("salesTime") String salesTime,
                            @RequestParam("genre") String genre,
                            @RequestParam("hashtag") String hashtag,
                            @RequestParam("communityId") Integer communityId,
                            @RequestParam(value = "image", required = false) MultipartFile image,
                            @PathVariable("id") Integer thread_id) throws IOException {
        String encode = null;

        if (image != null) {
            byte[] bytes = image.getBytes();
            encode = Base64.getEncoder().encodeToString(bytes);
        }

        //System.out.println("ハッシュタグ" + hashtag);

        if ("null".equals(hashtag)) {
            hashtag = null;
            //System.out.println("ハッシュタグ" + hashtag);
        }

        var result = kuchannelService.threadUpdate(new ThreadAddForm(threadName, furigana, address, salesTime, genre, hashtag, communityId, encode), thread_id);

        return result;

    }

    //管理者用。メンバー編集用
    @PutMapping("/memberSetting/{communityId}")
    public int memberSetting(@PathVariable("communityId") Integer communityId,@RequestBody List<AccountInformation> updateInfo){
        //System.out.println(updateInfo);
        var result =kuchannelService.memberSetting(updateInfo, communityId);
        return result;
    }

    @PutMapping("updateCommunityName/{communityId}/{newCommunityName}")
    public int updateCommunityName(@PathVariable("communityId") Integer communityId,@PathVariable("newCommunityName") String newCommunityName){
        System.out.println(newCommunityName);
        var result =kuchannelService.updateCommunityName(communityId,newCommunityName);
        return result;
    }

    //管理者用。コミュニティを消すよう。
    @DeleteMapping("/deleteCommunity/{communityId}")
    public int deleteCommunity(@PathVariable("communityId")Integer communityId){
        var result =kuchannelService.deleteCommunity(communityId);
        return result;
    }



    /*----------------------------------------*/

    @GetMapping("/getUrl")
    public String getUrl(@RequestParam("name") Integer id) {
        //System.out.println("コミュニティURL:" + kuchannelService.getUrl(id).url());
        return kuchannelService.getUrl(id).url();
    }

    //レビュー一覧取得
    @GetMapping("/getReviews")
    public List<ReviewElementAll> getReviews(@RequestParam("threadId") Integer threadId) {

        //スレッドIDをもとにコミュニティIDを入手
        var thread = kuchannelService.getThread(threadId);

        //データベースからレビュー一覧に表示する情報を全件取得
        var revs = kuchannelService.findReviewAll(threadId);

        //画像込みのレビュー情報を受け取るリスト
        List<ReviewElementAll> reviews = new ArrayList<>();

        for (var rev : revs) {
            //ユーザーIDとコミュニティIDをもとにニックネームを入手
            var reviewAccount = kuchannelService.getAccountInfoNick(rev.userId(), thread.getCommunity_id());

            //データベースからレビューの画像情報を取得する
            var reviewImages = kuchannelService.getReviewImages(rev.reviewId());

            //データベースからレビューの返信情報を取得する
            var reviewReplies = kuchannelService.getReviewReply(rev.reviewId());

            for (ReviewReply reviewReply : reviewReplies) {
                //ユーザーIDとコミュニティIDをもとにニックネームを入手
                var replyAccount = kuchannelService.getAccountInfoNick(reviewReply.getUserId(), thread.getCommunity_id());

                reviewReply.setUserName(replyAccount.getName());

            }

            //データベースからレビューのいいね件数を取得する
            var goodCount = kuchannelService.getGoodReview(rev.reviewId());

            reviews.add(new ReviewElementAll(rev.userId(), rev.userName(), rev.reviewId(), rev.title(),
                    rev.review(), rev.createDate(), reviewImages, reviewReplies, goodCount));

        }

        return reviews;
    }

    //レビューの編集・更新機能処理
    @PutMapping("/review-update")
    public int update(@RequestParam("id") Integer id,
                      @RequestParam("title") String title,
                      @RequestParam("content") String content,
                      @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        //reviewsテーブルのレコードの更新処理
        //var reviewUpdate = kuchannelService.reviewUpdate(reviewUpdateForm.getId(),reviewUpdateForm.getTitle(),reviewUpdateForm.getContent());
        var reviewUpdate = kuchannelService.reviewUpdate(id, title, content);

        //review_imagesテーブルに入ってたデータを削除
        kuchannelService.deleteImages(id);

        //System.out.println("images:" + images);

        if (images != null) {
            for (MultipartFile image : images) {
                //アップロード画像をバイト値に変換
                byte[] bytes = image.getBytes();

                //画像をエンコード
                String encode = Base64.getEncoder().encodeToString(bytes);

                //review_Imagesテーブルにインサート処理
                kuchannelService.reviewImagesInsert(id, encode);

            }
        }

        return reviewUpdate;

    }


    //レビュー投稿処理
    @PostMapping("/add-review")
    public ResponseEntity<String> addReview(@RequestParam("title") String title,
                                            @RequestParam("content") String content,
                                            @RequestParam("threadId") Integer threadId,
                                            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {

        //入力値チェック(本文)
        if ("".equals(content)) {
            return ResponseEntity.badRequest().body("");
        }

//        //入力値チェック(タイトル)
//        if (title.length() > 50) {
//            return ResponseEntity.badRequest().body("タイトルが長すぎます。50文字以内で入力してください。");
//        }

        //reviewsテーブルにインサート処理かつインサートしたIDを取得(thread_id 固定)
        var user = (UserRecord)session.getAttribute("user");
        int reviewId = kuchannelService.reviewInsert(user.id(), threadId, title, content);


        if (images != null) {
            for (MultipartFile image : images) {
                //アップロード画像をバイト値に変換
                byte[] bytes = image.getBytes();

                //画像をエンコード
                String encode = Base64.getEncoder().encodeToString(bytes);

                //review_Imagesテーブルにインサート処理
                kuchannelService.reviewImagesInsert(reviewId, encode);

            }
        }

        return ResponseEntity.ok("登録処理に成功しました。");

    }

    //レビュー返信登録処理
    @PostMapping("add-reply")
    public ReviewReply addReply(@RequestParam("id") Integer reviewId,
                                           @RequestParam("content") String content) {
        //repliesテーブルにインサート処理
        var user = (UserRecord)session.getAttribute("user");
        var reply = kuchannelService.replyInsert(user.id(), reviewId, content);

        return reply;

    }

    //スレッド情報を取得する
    @GetMapping("/getThread")
    public CommunityThread getThread(@RequestParam("threadId") Integer threadId) {
        var thread = kuchannelService.getThread(threadId);

        return thread;
    }

    //ジャンルを取得する
    @GetMapping("/getGenres")
    public List<GenreRecord> getGenre() {
        var genre = kuchannelService.getGenres();

        //System.out.println("ジャンル：" + genre);

        return genre;
    }

    //人気のハッシュタグを取得
    @GetMapping("/getPopularHashtags/{communityId}")
    public List<PopularHashTag> getHashtags(@PathVariable("communityId")Integer communityId) {
        var hashtags = kuchannelService.getPopularHashtags(communityId);

        for (var hashtag : hashtags) {
            hashtag.setTagName("#" + hashtag.getTagName());
        }

        //System.out.println("ハッシュタグ上位5件：" + hashtags);

        return hashtags;
    }

    //ハッシュタグ全件を取得
    @GetMapping("/getAllHashtags/{communityId}")
    public List<HashTag> getAllHashtags(@PathVariable("communityId")Integer communityId) {
        var hashtags = kuchannelService.getAllHashtags(communityId);
        System.out.println("ハッシュタグ全件：" + hashtags);
        return hashtags;
    }

    //キーワードでスレッドを絞り込む
    @GetMapping("/keywordThreads")
    public List<CommunityThread> keyThreads(@RequestParam("keyword") String keyword
                                            ,@RequestParam("communityId") Integer communityId
                                            ) {
        //keywordを空白(半角または全角)ごとに分けて格納
        String[] splittedKeywords = keyword.split("[\\s\\p{Z}]");
        //"#"と"＃"を除いたキーワードを格納。
        List<String> filteredKeywords = new ArrayList<>();
        for (String key : splittedKeywords) {
            if (!key.isEmpty() && !key.startsWith("#") && !key.startsWith("＃")) {
                filteredKeywords.add(key);
            }
        }
        String[] keywords = filteredKeywords.toArray(new String[0]);
        //もし検索欄に何も書いていなければ全件取得
        if(keywords.length == 0){
            return kuchannelService.communityThreads(communityId);

        }

        //キーワードとスレッドタイトルであいまい検索
        var threads = kuchannelService.findKeyThread(communityId, keywords);

        return threads;

    }

    //キーワードでレビューからスレッドを絞り込む
    @GetMapping("/keywordReviews")
    public List<FindThreadReviews> keyReviews(@RequestParam("communityId") Integer communityId,
                                            @RequestParam("keyword") String keyword) {
        //keywordを空白(半角または全角)ごとに分けて格納
        String[] keywords = keyword.split("[\\s\\p{Z}]");

        //キーワードとレビュータイトルと本文であいまい検索
        var threads = kuchannelService.findKeyThreadReview(communityId, keywords);

        List<FindThreadReviews> threadReviews = new ArrayList<>();

        for (var thread : threads) {
            //System.out.println("スレッドID:" + thread.getThreadId() + "キーワード：" + keyword);
            //スレッドごとにキーワード検索
            var reviews = kuchannelService.findKeyReview(thread.getThreadId(), keywords);
            threadReviews.add(new FindThreadReviews(thread.getThreadId(), thread.getUserId(), thread.getCommunityId(), thread.getTitle(),
                    thread.getGenre(), thread.getGood_count(), thread.getImage_path(), reviews));
        }

        return threadReviews;
    }

    //スレッドIDをもとにコミュニティIDを取得する
    @GetMapping("/getCommunityId")
    public CommunityThread getCommunityId(@RequestParam("threadId") Integer threadId) {
        var community = kuchannelService.getThread(threadId);

        return community;
    }

    /*-------------------------------------*/
    @DeleteMapping("/api/delete/{reviewId}")
    public int deleteReview(@PathVariable("reviewId") Integer reviewId) {
        int success = kuchannelService.reviewDelete(reviewId);
        return success;
    }
    /*-------------------------------------*/

    //スレッド一覧でコミュニティ名を出す用。すでにあったfindCommunityを利用。
    @GetMapping("/getCommunityName/{communityId}")
    public CommunityRecord getCommunityName(@PathVariable Integer communityId) {
        return kuchannelService.findCommunity(communityId);
    }

    //スレッド一覧でコミュニティ名を出す用。すでにあったfindCommunityを利用。
    @GetMapping("/getCommunityMember/{communityId}")
    public List<AccountInformation> getCommunityMember(@PathVariable Integer communityId) {
        return kuchannelService.getCommunityMember(communityId);
    }


    //統合処理。スレッド情報をもとに新しいスレッドを作成&スレッドいいねとスレッドハッシュタグを編集。
    @PutMapping("/IntegrateThreads")
    public int IntegrateThreads(@RequestBody ThreadAddForm threadInfo){
        var user = (UserRecord)session.getAttribute("user");
        kuchannelService.IntegrateThreads(threadInfo, user.id());
        //System.out.println(threadInfo);
        return 1;
    }






}
