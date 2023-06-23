package com.example.Kuchannel.controller;

import com.example.Kuchannel.KuchannelApplication;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import com.example.Kuchannel.form.ReviewUpdateForm;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    @GetMapping("getThreads")
    public List<CommunityThread> getThreads(){
//        コミュニティidを渡すようにする（今は1で固定）
        var threads = kuchannelService.communityThreads(1);
        return threads;
    }

    //スレッドのグッド
    @GetMapping("/goodDeal/thread/{threadId}")
    public int goodDealThread(@PathVariable("threadId")Integer thread_id){
//        セッションからuser_idをもらう一旦1で固定
        var user = (UserRecord)session.getAttribute("user");
        return kuchannelService.goodDealThread(thread_id, user.id());
    }

    //レビューのグッド
    @GetMapping("/goodDeal/review/{reviewId}")
    public int goodDealReview(@PathVariable("reviewId")Integer reviewId){
        var user = (UserRecord)session.getAttribute("user");
        return kuchannelService.goodDealReview(reviewId, user.id());
    }

    //スレッド削除用
    @DeleteMapping("/deleteThread/{threadId}")
    public boolean deleteThread(@PathVariable("threadId")Integer thread_id){
        return kuchannelService.deleteThread(thread_id);
    }

    //セッション情報返せるつもり。
    @GetMapping("getSessionInfo")
    public UserRecord getSessionInfo(){
        var user = (UserRecord)session.getAttribute("user");
        return user;
    }

    //スレッド作成処理
    @PostMapping("/thread-add")
    public int addThread(@RequestBody ThreadAddForm inputData) {
        //threadsテーブルにINSERT処理
        var result =kuchannelService.threadInsert(inputData);
        return result;
    }

    @PutMapping("/updateThread/{id}")
    public int threadUpdate(@RequestBody ThreadAddForm inputData,@PathVariable("id") Integer thread_id){
        var result = kuchannelService.threadUpdate(inputData ,thread_id);

        var thread = kuchannelService.getThread(result);

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

        //データベースからレビュー一覧に表示する情報を全件取得
        var revs = kuchannelService.findReviewAll(threadId);

        //画像込みのレビュー情報を受け取るリスト
        List<ReviewElementAll> reviews = new ArrayList<>();

        for (var rev : revs) {
            //データベースからレビューの画像情報を取得する
            var reviewImages = kuchannelService.getReviewImages(rev.reviewId());

            //画像をbase64にエンコードする

            //データベースからレビューの返信情報を取得する
            var reviewReplies = kuchannelService.getReviewReply(rev.reviewId());

            //データベースからレビューのいいね件数を取得する
            var goodCount = kuchannelService.getGoodReview(rev.reviewId());

            reviews.add(new ReviewElementAll(rev.userId(), rev.userName(), rev.reviewId(), rev.title(),
                    rev.review(), rev.createDate(), reviewImages, reviewReplies, goodCount));

        }

        return reviews;
    }

    //画像をbase64にエンコードする処理
//    public List<ReviewImageRecord> changeBase64(List<ReviewImageRecord> reviewImages) {
//        for (var image : reviewImages) {
//
//        }
//    }

    //レビューの編集・更新機能処理
//    @PostMapping("/api/update")
//    public ResponseEntity<String> update(@RequestParam("title") String title,
//                                         @RequestParam("content") String content,
//                                         @RequestParam("images") List<MultipartFile> images){
//
//    }


    //レビューの編集・更新機能処理
    @PutMapping("/review-update")
    public int update(@RequestBody ReviewUpdateForm reviewUpdateForm){

        //reviewsテーブルのレコードの更新処理
        var reviewUpdate = kuchannelService.reviewUpdate(reviewUpdateForm.getId(),reviewUpdateForm.getTitle(),reviewUpdateForm.getContent());

        return reviewUpdate;

    }


    //レビュー投稿処理
    @PostMapping("/add-review")
    public ResponseEntity<String> addReview(@RequestParam("title") String title,
                                            @RequestParam("content") String content,
                                            @RequestParam("images") List<MultipartFile> images) {

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
        int reviewId = kuchannelService.reviewInsert(user.id(), 1, title, content);

        for (MultipartFile image : images) {
            // 画像を1枚ずつ保存処理
            var num = uploadImage(image);

            //review_Imagesテーブルにインサート処理
            kuchannelService.reviewImagesInsert(reviewId, num + image.getOriginalFilename());

        }

        return ResponseEntity.ok("登録処理に成功しました。");

    }

    //画像アップロード処理 戻り値に画像の名前が被らないように現在時刻を返す
    private long uploadImage(MultipartFile image) {
        //ファイル名を取得
        String fileName = image.getOriginalFilename();

        //現在時刻を取得(画像の名前が被らないようにするため)
        long currentTimeMillis = System.currentTimeMillis();

        //格納先パスを指定(ひとまず絶対パス)
        Path filePath = Paths.get("src/main/resources/static/images/reviewImages/" + currentTimeMillis + fileName);

        try {
            //アップロード画像をバイト値に変換
            byte[] bytes = image.getBytes();

            //String encode = Base64.getEncoder().encodeToString(bytes);

            //System.out.println(encode);

            // Base64デコード
            //byte[] decodedBytes = Base64.getDecoder().decode(encode);



            //バイト値を書き込むファイルを作成し、指定したパスに格納
            OutputStream stream = Files.newOutputStream(filePath);
            stream.write(bytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return currentTimeMillis;

    }

    //レビュー返信登録処理
    @PostMapping("add-reply")
    public ReviewReplyRecord addReply(@RequestParam("id") Integer reviewId,
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

        System.out.println("ジャンル：" + genre);

        return genre;
    }

    //人気のハッシュタグを取得
    @GetMapping("/getHashtags")
    public List<HashTagRecord> getHashtags() {
        var hashtags = kuchannelService.getHashtags();

        System.out.println("ハッシュタグ上位5件：" + hashtags);

        return hashtags;
    }

    //キーワードでスレッドを絞り込む
    @GetMapping("/keywordThreads")
    public List<CommunityThread> keyThreads(@RequestParam("keyword") String keyword) {
        //keywordを空白(半角または全角)ごとに分けて格納
        String[] keywords = keyword.split("[\\s\\p{Z}]");

        //キーワードとスレッドタイトルであいまい検索
        var threads = kuchannelService.findKeyThread(keywords);

        return threads;

    }

    /*-------------------------------------*/
    @DeleteMapping("/api/delete/{reviewId}")
    public int deleteReview(@PathVariable("reviewId") Integer reviewId) {
        int success = kuchannelService.reviewDelete(reviewId);
        return success;
    }
    /*-------------------------------------*/

}
