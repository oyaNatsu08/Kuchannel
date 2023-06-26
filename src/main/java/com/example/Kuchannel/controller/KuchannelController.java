package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.*;
import com.example.Kuchannel.entity.InformatonRecord;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class KuchannelController {

    @Autowired
    private KuchannelService kuchannelService;

    @Autowired
    private HttpSession session;

    /*------------------------login(ログイン)-----------------------------------*/
    @GetMapping("/login")
    public String user(@ModelAttribute("UserForm") UserForm userForm) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("UserForm") UserForm userForm,
                        BindingResult bindingResult,
                        @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                        Model model) {
        //バリデーション
        if (bindingResult.hasErrors()) {
            return "login";
        }

        String loginId = userForm.getLoginId();
        String password = userForm.getPassword();

        var user = kuchannelService.Login(loginId, password);

        if (user == null) {
            model.addAttribute("error", "IDまたはパスワードが不正です");
            return "login";
        }

        //セッション
        session.setAttribute("user", user);
        model.addAttribute("image", user.imagePath());

        return "my-page";

    }
    /*------------------------------------------------------------------------*/


    /*------------------------logout(ログアウト)-----------------------------------*/
    @GetMapping("/logout")
    public String logout(@ModelAttribute("UserForm") UserForm userForm) {
        session.invalidate();
        return "logout";
    }

    /*------------------------------------------------------------------------*/


    /*------------------------プロフィール画面-----------------------------------*/

    @GetMapping("/profile-details")
    public String profileDetail(Model model){

        UserRecord userData = (UserRecord) session.getAttribute("user");
        var user_id = userData.loginId();

        var list = model.addAttribute("profile", kuchannelService.detail(user_id));
        return "profile-details";
    }

    /*------------------------------------------------------------------------*/


    /*-------------------------Insert(新規追加)--------------------------*/
    @GetMapping("/create-user")
    public String accountAdd(@ModelAttribute("CreateForm") CreateForm createForm) {
        return "create-user";
    }

    @PostMapping("create-user")
    public String accountAdd(@Validated @ModelAttribute("CreateForm") CreateForm createForm,
                             BindingResult bindingResult,
                             Model model) throws IOException {
        //バリデーション
        if (bindingResult.hasErrors()) {
            return "create-user";
        }
        String loginId = createForm.getLoginId();
        String password = createForm.getPassword();
        String name = createForm.getName();
        //String image_path = createForm.getImage_path();

        File directory = new File("src/main/resources/static/images/icons");

        File[] files = directory.listFiles();

        List<File> imageFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        int randomIndex = new Random().nextInt(imageFiles.size());
        File randomImageFile = imageFiles.get(randomIndex);

        byte[] bytes = Files.readAllBytes(randomImageFile.toPath());

        String encode = Base64.getEncoder().encodeToString(bytes);

        CreateRecord create = new CreateRecord(loginId, password, name, encode);
        kuchannelService.create(loginId, password, name, encode);
        return "redirect:/login";
    }

    // 画像ファイルの拡張子を確認するメソッド
    private boolean isImageFile(File file) {
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
    }

    /*----------------------------------------*/

    //マイページの表示
    @GetMapping("/mypage")
    public String myPage(@ModelAttribute("UserForm") UserForm userForm,
                         @ModelAttribute("communityAdd") CommunityAddForm communityAddForm) {
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
            return "my-page";
        }
    }

   /*-----------------------------------------*/

    //コミュニティ作成処理
    @PostMapping("/community-add")
    public String communityAdd(@Validated @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "my-page";
        } else {
            String inviteCode = communityAddForm.getCommunityName();

            String randomString;
            do {
                //ランダムな文字列を生成
                randomString = generateRandomString();
            } while (kuchannelService.checkUrl(randomString, communityAddForm.getCommunityName()) != null); //テーブルにurlが存在しなければループを抜ける ////urlが重複しないかチェック

            //コミュニティを作成する処理と作成したIDを受け取る
            int communityId = kuchannelService.communityInsert(communityAddForm.getCommunityName(), "http://localhost:8080/community/" + randomString + "/" + communityAddForm.getCommunityName());

            //コミュニティユーザーテーブルにインサートする処理
            var user = (UserRecord)session.getAttribute("user");

            //もしニックネームがnullならユーザーネームを登録する
            if ("".equals(communityAddForm.getNickName())) {
                kuchannelService.communityUserInsert(user.id(), communityId, user.name(), 2);
            } else {
                kuchannelService.communityUserInsert(user.id(), communityId, communityAddForm.getNickName(), 2);
            }

            model.addAttribute("communityId", communityId);
            model.addAttribute("communityName", inviteCode);

            return "thread-list";
        }

    }

    //ランダムな文字列を生成するメソッド
    public static String generateRandomString() {
        UUID uuid = UUID.randomUUID();
        String randomString = uuid.toString().replace("-", "");
        return randomString;
    }

    //任意のスレッド一覧にとぶ(コミュニティへ飛ぶ、URLで飛ぶ場合)
    @GetMapping("/community/{randomString}/{code}")
    public String threadView(@ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                             @ModelAttribute("communityJoin") CommunityJoinForm communityForm,
                             @ModelAttribute("UserForm") UserForm userForm,
                             @PathVariable("randomString") String str,
                             //コミュニティの名前
                             @PathVariable("code") String code,
                             Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
            //コミュニティに初めて参加する場合は、参加確認画面へ飛んで、community_userテーブルを更新
            //コミュニティに参加しているかチェック
            var user = (UserRecord)session.getAttribute("user");
            var communityUser = kuchannelService.checkJoin(user.id(), "http://localhost:8080/community/" + str + "/" + code);
            if (communityUser == null) {
                model.addAttribute("communityName", code);
                model.addAttribute("url", "http://localhost:8080/community/" + str + "/" + code);
                //参加確認画面へ
                return "community-join";
            } else {

                var community = kuchannelService.getCommunity("http://localhost:8080/community/" + str + "/" + code);

                model.addAttribute("communityId", community.id());
                model.addAttribute("communityName", code);
                return "thread-list";
            }
        }
    }

    //お知らせ画面遷移
    @GetMapping("/notice")
    public String noticeView(@ModelAttribute("UserForm") UserForm userForm,
                             Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //お知らせ一覧を表示する
            var user = (UserRecord)session.getAttribute("user");
            List<NoticeReplyRecord> notices = kuchannelService.userNotice(user.id());

            //お知らせ一覧のユーザーIDをもとに、返信ユーザーを取得する
            List<NoticeReplyRecord2> notices2 = new ArrayList<>();
            for (NoticeReplyRecord notice : notices) {
                UserRecord replyUser = kuchannelService.findUser(notice.replyUserId());
                notices2.add(new NoticeReplyRecord2(replyUser.name(), notice.threadId(), notice.threadTitle(), notice.noticeId(), notice.flag(), notice.reviewId()));
            }

            //お問い合わせ情報を表示する
            List<InquiryRecord> inquires = kuchannelService.userInquiry(user.id());

            model.addAttribute("notices", notices2);
            model.addAttribute("inquires", inquires);

            return "notice-list";

        }
    }

    //コミュニティに参加する処理
    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("communityJoin") CommunityJoinForm communityForm,
                       BindingResult bindingResult,
                       @RequestParam ("url") String url,
                       Model model) {
        if (bindingResult.hasErrors()) {
            //参加したいコミュニティを特定する
            CommunityRecord community = kuchannelService.getCommunity(url);

            model.addAttribute("communityName", community.name());

            return "community-join";
        } else {

            //参加したいコミュニティを特定する
            CommunityRecord community = kuchannelService.getCommunity(url);

            var user = (UserRecord)session.getAttribute("user");
            String nickName;
            //もしニックネームがnullならユーザーネームを登録する
            if ("".equals(communityForm.getJoinNickName())) {
                nickName = user.name();
            } else {
                nickName = communityForm.getJoinNickName();
            }

            //コミュニティに以前参加していたか確認
            if (kuchannelService.checkJoin(user.id(), community.id()) != null) {

                //community_userテーブルをアップデート
                kuchannelService.communityUserUpdate(user.id(), community.id(), nickName);

            } else {

                //コミュニティユーザーテーブルにインサートする処理
                kuchannelService.communityUserInsert(user.id(), community.id(), nickName, 1); //Integer.parseInt(session.getAttribute("userId").toString())

            }

            model.addAttribute("communityId", community.id());
            model.addAttribute("communityName", community.name());

            return "thread-list";

        }
    }

    //お問い合わせ詳細画面へ
    @GetMapping("/inquiry")
    public String inquiryDetailView(@RequestParam("id") Integer inquiryId,
                                    @ModelAttribute("UserForm") UserForm userForm,
                                    Model model) {
        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            InquiryDetailRecord inquiryDetail = kuchannelService.findInquiry(inquiryId);
            UserRecord user = kuchannelService.findUser(inquiryDetail.userId());
            CommunityRecord community = kuchannelService.findCommunity(inquiryDetail.communityId());

            model.addAttribute("inquiry", inquiryDetail);
            model.addAttribute("userName", user.name());
            model.addAttribute("community", community);

            return "inquiry-detail";
        }
    }



    //コミュニティのスレッド一覧へ遷移する(ボタンなどでの遷移)
    @GetMapping("/community/thread-list/{communityId}")
    public String threadListView(@PathVariable("communityId") Integer communityId,
                                 Model model) {
        //コミュニティIDを元にコミュニティを特定する
        CommunityRecord community = kuchannelService.findCommunity(communityId);
        model.addAttribute("communityId",communityId );
        model.addAttribute("communityName", community.name());
        System.out.println(community.name());
        model.addAttribute("name", community.name());

        return "thread-list";
    }

    //お問い合わせページ
    @GetMapping("/Information")
    public String info(@ModelAttribute("informationForm") InformationForm informationForm) {
        return "Information";
    }

    @PostMapping("/Information")
    public String information(@Validated @ModelAttribute("informationForm") InformationForm informationForm,
                              BindingResult bindingResult,
//                              @RequestParam(name="communityId") Integer communityId,
                              Model model){

        //バリデーション
        if (bindingResult.hasErrors()){
            return "Information";
        }

        var userData = (UserRecord)session.getAttribute("user");
        //セッションのユーザーID
        var userId = userData.id();
        //var userId = 1;
        var communityId = 1;
        String content = informationForm.getInformation();
        boolean flag = false;

        //お問い合わせ情報の処理を行う
        InformatonRecord informatonRecord = new  InformatonRecord(userId,communityId,content,flag);

        var informationDetails = kuchannelService.information(informatonRecord);

        return "thread-list";

    }
    //スレッドのレビュー一覧へ飛ぶ
    @GetMapping("/thread-review/{threadId}")
    public String reviewListView(@PathVariable("threadId") Integer threadId,
                                 @ModelAttribute("UserForm") UserForm userForm,
                                 Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //スレッドIDをもとに、スレッド情報を取得する
            var thread = kuchannelService.getThread(threadId);

            //ユーザー情報を取得
            var user = (UserRecord)session.getAttribute("user");

            model.addAttribute("thread", thread);
            model.addAttribute("userId", user.id());

            return "review-list";
        }
    }

    //レビュー作成処理
    @PostMapping("/review-add")
    public String reviewAdd(Model model) {

        return "review-list";
    }

    //レビュー詳細画面へ
    @GetMapping("/review-detail")
    public String reviewDetail(@RequestParam("reviewId") Integer reviewId,
                               @RequestParam("threadId") Integer threadId,
                               @RequestParam(value = "noticeId", required = false) Integer noticeId,
                               @RequestParam(value = "flag", required = false) Boolean readFlag,
                               Model model) {

        //スレッドIDをもとにコミュニティIDを入手
        var thread = kuchannelService.getThread(threadId);

        //レビューIDを元にreviewsテーブルから情報を取得する
        var review = kuchannelService.findReview(reviewId);

        //ユーザーIDとコミュニティIDをもとにニックネームを入手
        var reviewAccount = kuchannelService.getAccountInfoNick(review.userId(), thread.getCommunity_id());

        //データベースからレビューの画像情報を取得する
        var reviewImages = kuchannelService.getReviewImages(reviewId);

        //データベースからレビューの返信情報を取得する
        var reviewReplies = kuchannelService.getReviewReply(reviewId);

        for (ReviewReply reviewReply : reviewReplies) {
            //ユーザーIDとコミュニティIDをもとにニックネームを入手
            var replyAccount = kuchannelService.getAccountInfoNick(reviewReply.getUserId(), thread.getCommunity_id());

            reviewReply.setUserName(replyAccount.getName());

        }

        //データベースからレビューのいいね件数を取得する
        var goodCount = kuchannelService.getGoodReview(reviewId);

        model.addAttribute("review", new ReviewElementAll(review.userId(), reviewAccount.getName(), review.reviewId(), review.title(),
                review.review(), review.createDate(), reviewImages, reviewReplies, goodCount));
        model.addAttribute("reviewId", reviewId);
        model.addAttribute("threadId", threadId);

        //お知らせ画面からの遷移か判断する, 未読フラッグがtrueならfalseに変える
        if ((noticeId != null) && (readFlag)) {
            //お知らせテーブルの未読フラッグをアップデート
            kuchannelService.readNotice(noticeId);
        }

        return "review-detail";

    }

    //ユーザーのレビュー一覧画面に遷移
    @GetMapping("/user-review/{userId}")
    public String userReviewView(@PathVariable("userId") Integer userId,
                                 Model model) {

        //ユーザーのレビュー一覧に必要な情報を取得する
        var reviews = kuchannelService.getUserReview(userId);

        model.addAttribute("reviews", reviews);

        return "user-review-list";
    }


    /*------------------------------------------------*/


    //    /*-------------------------Update(プロフィール編集)--------------------------*/
    @GetMapping("/profile-edit")
    public String profileUpdate(@RequestParam("name") String name,
                                @RequestParam("loginId") String loginId,
                                @RequestParam("password") String password,
                                @RequestParam("imagePath") String imagePath,
                                @ModelAttribute("EditForm")EditForm editForm,
                                Model model){

        UserRecord user = new UserRecord(null, loginId, name, password, imagePath);

        model.addAttribute("user", user);

        return "profile-edit";
    }

    @PostMapping("profile-edit")
    public String profileUpdate(@Validated @ModelAttribute("EditForm") EditForm editForm,
                                BindingResult bindingResult,
                                Model model){
        //バリデーション
        if(bindingResult.hasErrors()){
            return "profile-edit";
        }
        String loginId = editForm.getLoginId();
        String name = editForm.getName();
        String password = editForm.getPassword();
        kuchannelService.edit(loginId, name, password);
        UserRecord userData = (UserRecord) session.getAttribute("user");
        var user_id = userData.loginId();

        model.addAttribute("profile", kuchannelService.detail(user_id));

        return "/profile-details";
    }
//    /*------------------------------------------------------------------------*/
}
