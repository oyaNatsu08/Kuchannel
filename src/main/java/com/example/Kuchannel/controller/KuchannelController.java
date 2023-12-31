package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.*;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class KuchannelController {

    @Autowired
    private KuchannelService kuchannelService;

    @Autowired
    private HttpSession session;

    /*------------------------login(ログイン)-----------------------------------*/
    @GetMapping("/kuchannel/login")
    public String user(@ModelAttribute("UserForm") UserForm userForm,
                       @ModelAttribute("communityAdd") CommunityAddForm communityAddForm) {

        return "login";
    }

    @PostMapping("/kuchannel/login")
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

        return "redirect:/kuchannel/mypage";

    }
    /*------------------------------------------------------------------------*/


    /*------------------------logout(ログアウト)-----------------------------------*/
    @GetMapping("/kuchannel/logout")
    public String logout(@ModelAttribute("UserForm") UserForm userForm) {
        session.invalidate();
        return "logout";
    }

    /*------------------------------------------------------------------------*/


    /*------------------------プロフィール画面-----------------------------------*/

    @GetMapping("/kuchannel/profile-details")
    public String profileDetail(Model model) {

        UserRecord userData = (UserRecord) session.getAttribute("user");
        var user_id = userData.loginId();

        var list = model.addAttribute("profile", kuchannelService.detail(user_id));

        model.addAttribute("image", userData.imagePath());

        return "/profile-details";
    }

    /*------------------------------------------------------------------------*/


    /*-------------------------Insert(新規追加)--------------------------*/
    @GetMapping("/kuchannel/create-user")
    public String accountAdd(@ModelAttribute("CreateForm") CreateForm createForm) {
        return "create-user";
    }

    @PostMapping("/kuchannel/create-user")
    public String accountAdd(@Validated @ModelAttribute("CreateForm") CreateForm createForm,
                             BindingResult bindingResult,
                             Model model) throws IOException {
        //バリデーション
        if (bindingResult.hasErrors()) {
            return "create-user";
        }
        if (kuchannelService.checkExistingLoginId(createForm.getLoginId()) != null) {
            model.addAttribute("error", "そのアカウントIDは既に存在します");
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

        int randomIndex = ThreadLocalRandom.current().nextInt(imageFiles.size());

        //System.out.println(randomIndex);
        File randomImageFile = imageFiles.get(randomIndex);

        byte[] bytes = Files.readAllBytes(randomImageFile.toPath());

        String encode = Base64.getEncoder().encodeToString(bytes);

        CreateRecord create = new CreateRecord(loginId, password, name, encode);
        kuchannelService.create(loginId, password, name, encode);
        return "redirect:/kuchannel/login";
    }

    // 画像ファイルの拡張子を確認するメソッド
    private boolean isImageFile(File file) {
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("webp");
    }

    /*----------------------------------------*/

    //マイページの表示
    @GetMapping("/kuchannel/mypage")
    public String myPage(@ModelAttribute("UserForm") UserForm userForm,
                         @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                         Model model) {
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
            var user = (UserRecord) session.getAttribute("user");

            model.addAttribute("image", user.imagePath());
            return "my-page";
        }
    }

    /*-----------------------------------------*/

    //コミュニティ作成処理
    @PostMapping("/kuchannel/community-add")
    public String communityAdd(@Validated @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                               BindingResult bindingResult, Model model) {

        //コミュニティユーザーテーブルにインサートする処理
        var user = (UserRecord) session.getAttribute("user");

        model.addAttribute("image", user.imagePath());

        if (bindingResult.hasErrors()) {
            model.addAttribute("judge", false);
            return "my-page";
        } else {
            String inviteCode = communityAddForm.getCommunityName();

            String randomString;
            do {
                //ランダムな文字列を生成
                randomString = generateRandomString();
            } while (kuchannelService.checkUrl(randomString, communityAddForm.getCommunityName()) != null); //テーブルにurlが存在しなければループを抜ける ////urlが重複しないかチェック

            //コミュニティを作成する処理と作成したIDを受け取る
            int communityId = kuchannelService.communityInsert(communityAddForm.getCommunityName(), "http://localhost:8080/kuchannel/community/" + randomString + "/" + communityAddForm.getCommunityName());


            //もしニックネームがnullならユーザーネームを登録する
            if ("".equals(communityAddForm.getNickName())) {
                kuchannelService.communityUserInsert(user.id(), communityId, user.name(), 2);
            } else {
                kuchannelService.communityUserInsert(user.id(), communityId, communityAddForm.getNickName(), 2);
            }

            model.addAttribute("communityId", communityId);
            model.addAttribute("communityName", inviteCode);
            model.addAttribute("image", user.imagePath());

            var community =kuchannelService.findCommunity(communityId);
            String url = community.url();
            String moldedUrl =url.replace("http://localhost:8080/","");

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace( "%2F","/") + "/" + community.id() + "/threads";

            var community = kuchannelService.findCommunity(communityId);
            String url = community.url();
            String moldedUrl = url.replace("http://192.168.33.99:8080/", "");

            model.addAttribute("judge", true);

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads";

            var community = kuchannelService.findCommunity(communityId);
            String url = community.url();
            String moldedUrl = url.replace("http://localhost:8080/", "");

            model.addAttribute("judge", true);

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads";

        }

    }

    //ランダムな文字列を生成するメソッド
    public static String generateRandomString() {
        UUID uuid = UUID.randomUUID();
        String randomString = uuid.toString().replace("-", "");
        return randomString;
    }

    //コミュニティのスレッド一覧へ遷移する(ボタンなどでの遷移)
    @GetMapping("/kuchannel/community/{randomString}/{code}/{communityId}/threads")

    public String threadListView2(@ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                                  @ModelAttribute("communityJoin") CommunityJoinForm communityForm,
                                  @ModelAttribute("UserForm") UserForm userForm,
                                  @PathVariable("randomString") String str,
                                  //コミュニティの名前
                                  @PathVariable("code") String code,
                                  @PathVariable("communityId") Integer communityId,
                                  Model model) {
//        //コミュニティIDを元にコミュニティを特定する
//        CommunityRecord community = kuchannelService.findCommunity(communityId);
//        model.addAttribute("communityId",communityId);
//        model.addAttribute("communityName", community.name());
////        model.addAttribute("name", community.name());

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
            //コミュニティに初めて参加する場合は、参加確認画面へ飛んで、community_userテーブルを更新
            //コミュニティに参加しているかチェック
            var user = (UserRecord) session.getAttribute("user");

            model.addAttribute("image", user.imagePath());

            var communityUser = kuchannelService.checkJoin(user.id(), "http://localhost:8080/kuchannel/community/" + str + "/" + code);
            if (communityUser == null) {
                model.addAttribute("communityName", code);
                model.addAttribute("url", "http://localhost:8080/kuchannel/community/" + str + "/" + code);
                //参加確認画面へ
                return "community-join";
            } else {

                var community = kuchannelService.getCommunity("http://192.168.33.99:8080/kuchannel/community/" + str + "/" + code);

                if (community.deleteDate() == null) {
                    model.addAttribute("communityId", community.id());
                    model.addAttribute("communityName", code);
                    model.addAttribute("communityUrl", community.url());

                    return "thread-list";
                } else {
                    return "deleted-community";
                }

            }

        }
    }

    //レビュー一覧に飛ぶ。消されていたら飛べない。
    @GetMapping("/kuchannel/community/{randomString}/{code}/{communityId}/threads/{threadId}/reviews")
    public String reviewListView2(@PathVariable("threadId") Integer threadId,
                                  @ModelAttribute("UserForm") UserForm userForm,
                                  @PathVariable("communityId") Integer communityId,
                                  Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //スレッドIDをもとに、スレッド情報を取得する
            var thread = kuchannelService.getThread(threadId);
            var community = kuchannelService.findCommunity(communityId);

            //ユーザー情報を取得
            var user = (UserRecord) session.getAttribute("user");
            model.addAttribute("image", user.imagePath());

            if (community.deleteDate() == null) {

                model.addAttribute("thread", thread);
                model.addAttribute("userId", user.id());
                model.addAttribute("community", community);

                return "review-list";
            } else {
                return "deleted-community";
            }

        }
    }

    //お知らせ画面遷移
    @GetMapping("/kuchannel/notice")
    public String noticeView(@ModelAttribute("UserForm") UserForm userForm,
                             Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //お知らせ一覧を表示する
            var user = (UserRecord) session.getAttribute("user");
            List<NoticeReplyRecord> notices = kuchannelService.userNotice(user.id());

            //お知らせ一覧のユーザーIDをもとに、返信ユーザーを取得する
            List<NoticeReplyRecord2> notices2 = new ArrayList<>();
            for (NoticeReplyRecord notice : notices) {
                var thread = kuchannelService.getThread(notice.threadId());

                UserRecord replyUser = kuchannelService.findUser(notice.replyUserId());

                //ニックネームチェック
                var nickUser = kuchannelService.getAccountInfoNick(notice.replyUserId(), thread.getCommunity_id());

                notices2.add(new NoticeReplyRecord2(nickUser.getName(), notice.threadId(), notice.threadTitle(), notice.noticeId(), notice.flag(), notice.reviewId()));
            }

            //お問い合わせ情報を表示する
            List<Inquiry> inquires = kuchannelService.userInquiry(user.id());

            for(Inquiry inquiry : inquires) {
                //ニックネームチェック
                var nickUser = kuchannelService.getAccountInfoNick(inquiry.getInquiryUserId(), inquiry.getCommunityId());
                inquiry.setInquiryUserName(nickUser.getName());
            }

            model.addAttribute("image", user.imagePath());
            model.addAttribute("notices", notices2);
            model.addAttribute("inquires", inquires);

            return "notice-list";

        }
    }

    //レビュー詳細画面へ
    @GetMapping("/kuchannel/community/{randomString}/{code}/{communityId}/threads/{threadId}/reviews/{reviewId}")
    public String reviewDetail2(@PathVariable("reviewId") Integer reviewId,
                                @ModelAttribute("UserForm") UserForm userForm,
                                @PathVariable("threadId") Integer threadId,
                                Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //スレッドIDをもとにコミュニティIDを取得する
            var thread = kuchannelService.getThread(threadId);

            //レビューIDを元にreviewsテーブルから情報を取得する
            var review = kuchannelService.findReview(reviewId);

            //ユーザーIDとコミュニティIDをもとにニックネームを入手
            //System.out.println("コミュニティID" + thread.getCommunity_id() + "ユーザーID：" + rev.getUserId());
            var reviewAccount = kuchannelService.getAccountInfoNick(review.getUserId(), thread.getCommunity_id());
            review.setUserName(reviewAccount.getName());

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

            var user = (UserRecord) session.getAttribute("user");
            model.addAttribute("image", user.imagePath());

            model.addAttribute("review", new ReviewElementAll(review.getUserId(), review.getUserName(), review.getReviewId(), review.getTitle(),
                    review.getReview(), review.getCreateDate(), reviewImages, reviewReplies, goodCount));

            model.addAttribute("reviewId", reviewId);
            model.addAttribute("threadId", threadId);
            model.addAttribute("communityId", thread.getCommunity_id());

            return "review-detail";

        }

    }


    //コミュニティに参加する処理
    @PostMapping("/kuchannel/join")
    public String join(@Validated @ModelAttribute("communityJoin") CommunityJoinForm communityForm,
                       BindingResult bindingResult,
                       @RequestParam("url") String url,
                       Model model) {
        if (bindingResult.hasErrors()) {
            //参加したいコミュニティを特定する
            CommunityRecord community = kuchannelService.getCommunity(url);
            model.addAttribute("communityName", community.name());
            return "community-join";
        } else {

            //参加したいコミュニティを特定する
            CommunityRecord community = kuchannelService.getCommunity(url);

            var user = (UserRecord) session.getAttribute("user");
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

            model.addAttribute("image", user.imagePath());

            String moldedUrl = url.replace("http://localhost:8080/", "");
            String URlToJump = moldedUrl + "/" + community.id() + "/" + "/threads";

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads";


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

            var loginUser = (UserRecord) session.getAttribute("user");

            InquiryDetailRecord inquiryDetail = kuchannelService.findInquiry(inquiryId);
            UserRecord user = kuchannelService.findUser(inquiryDetail.userId());
            CommunityRecord community = kuchannelService.findCommunity(inquiryDetail.communityId());

            model.addAttribute("inquiry", inquiryDetail);
            model.addAttribute("userName", user.name());
            model.addAttribute("community", community);

            model.addAttribute("image", loginUser.imagePath());

            return "inquiry-detail";
        }
    }

    //コミュニティのスレッド一覧へ遷移する(ボタンなどでの遷移)
    @GetMapping("/community/thread-list/{communityId}")
    public String threadListView(@PathVariable("communityId") Integer communityId,
                                 Model model) {
        //コミュニティIDを元にコミュニティを特定する
        CommunityRecord community = kuchannelService.findCommunity(communityId);
        model.addAttribute("communityId", communityId);
        model.addAttribute("communityName", community.name());
        //System.out.println(community.name());
        model.addAttribute("name", community.name());

        var user = (UserRecord) session.getAttribute("user");

        model.addAttribute("image", user.imagePath());

        return "thread-list";
    }

    //お問い合わせページ
    @GetMapping("/kuchannel/Information")
    public String info(@RequestParam("communityId") Integer communityId,
                       @ModelAttribute("informationForm") InformationForm informationForm,
                       Model model) {

        var user = (UserRecord) session.getAttribute("user");

        model.addAttribute("image", user.imagePath());

        model.addAttribute("communityId", communityId);

        return "Information";
    }

    @PostMapping("/kuchannel/Information")
    public String information(@Validated @ModelAttribute("informationForm") InformationForm informationForm,
                              BindingResult bindingResult,
                              @RequestParam(name = "communityId") Integer communityId,
                              Model model) {

        var user = (UserRecord) session.getAttribute("user");
        model.addAttribute("image", user.imagePath());
        model.addAttribute("communityId", communityId);

        //バリデーション
        if (bindingResult.hasErrors()) {
            return "Information";
        }

        var userId = user.id();

        String content = informationForm.getInformation();
        boolean flag = false;

        //お問い合わせ情報の処理を行う
        InformatonRecord informatonRecord = new InformatonRecord(userId, communityId, content, flag);

        var community = kuchannelService.findCommunity(communityId);

        var informationDetails = kuchannelService.information(informatonRecord);

        model.addAttribute("communityName", community.name());

        String url = community.url();

        String moldedUrl = url.replace("http://localhost:8080/", "");

        return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads";

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
            var user = (UserRecord) session.getAttribute("user");

            model.addAttribute("image", user.imagePath());

            model.addAttribute("thread", thread);
            model.addAttribute("userId", user.id());
            model.addAttribute("community", kuchannelService.findCommunity(thread.getCommunity_id()));
            var community = kuchannelService.findCommunity(thread.getCommunity_id());
            String url = community.url();
            String moldedUrl = url.replace("http://localhost:8080/", "");

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads/" + threadId + "/reviews";

        }
    }

    //レビュー詳細画面へ
    @GetMapping("/review-detail")
    public String reviewDetail(@RequestParam("reviewId") Integer reviewId,
                               @RequestParam("threadId") Integer threadId,
                               @RequestParam(value = "noticeId", required = false) Integer noticeId,
                               @RequestParam(value = "flag", required = false) Boolean readFlag,
                               Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //スレッドIDをもとにコミュニティIDを入手
            var thread = kuchannelService.getThread(threadId);

            //レビューIDを元にreviewsテーブルから情報を取得する
            var review = kuchannelService.findReview(reviewId);

            //ユーザーIDとコミュニティIDをもとにニックネームを入手
            var reviewAccount = kuchannelService.getAccountInfoNick(review.getUserId(), thread.getCommunity_id());

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

            model.addAttribute("review", new ReviewElementAll(review.getUserId(), reviewAccount.getName(), review.getReviewId(), review.getTitle(),
                    review.getReview(), review.getCreateDate(), reviewImages, reviewReplies, goodCount));
            model.addAttribute("reviewId", reviewId);
            model.addAttribute("threadId", threadId);

            //お知らせ画面からの遷移か判断する, 未読フラッグがtrueならfalseに変える
            if ((noticeId != null) && (readFlag)) {
                //お知らせテーブルの未読フラッグをアップデート
                kuchannelService.readNotice(noticeId);
            }

            var user = (UserRecord) session.getAttribute("user");
            model.addAttribute("image", user.imagePath());

            var community = kuchannelService.findCommunity(thread.getCommunity_id());
            String url = community.url();
            String moldedUrl = url.replace("http://localhost:8080/", "");

            return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads/" + threadId + "/reviews/" + reviewId;
        }
    }

    //ユーザーのレビュー一覧画面に遷移
    @GetMapping("/kuchannel/user-review/{communityId}/{userId}")
    public String userReviewView(@PathVariable("userId") Integer userId,
                                 @ModelAttribute("UserForm") UserForm userForm,
                                 @PathVariable("communityId") Integer communityId,
                                 Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            //ユーザーのレビュー一覧に必要な情報を取得する
            var reviews = kuchannelService.getUserReview(userId);

            //ユーザーを特定する
            var targetUser = kuchannelService.findUser(userId);

            var reviewAccount = kuchannelService.getAccountInfoNick(targetUser.id(), communityId);

            model.addAttribute("reviews", reviews);
            model.addAttribute("userName", reviewAccount.getName());

            var user = (UserRecord) session.getAttribute("user");
            model.addAttribute("image", user.imagePath());

            return "user-review-list";

        }

    }

    /*-------------------------Update(プロフィール編集)--------------------------*/
    @GetMapping("/kuchannel/profile-edit")
    public String profileUpdate(@ModelAttribute("EditForm") EditForm editForm,
                                @ModelAttribute("UserForm") UserForm userForm,
                                Model model) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            var sessionUser = (UserRecord) session.getAttribute("user");
            UserRecord user = new UserRecord(null, sessionUser.loginId(), sessionUser.name(), sessionUser.password(), sessionUser.imagePath());

            model.addAttribute("user", user);
            model.addAttribute("loginId", sessionUser.loginId());
            model.addAttribute("image", sessionUser.imagePath());

            return "profile-edit";

        }

    }

    @PostMapping("/kuchannel/profile-edit")
    public String profileUpdate(@Validated @ModelAttribute("EditForm") EditForm editForm,
                                BindingResult bindingResult,
                                @RequestParam("loginId") String loginId,
                                @RequestParam(value = "updateImage", required = false) MultipartFile updateImage,

                                Model model) throws IOException {

        var user = (UserRecord) session.getAttribute(("user"));
        model.addAttribute("image", user.imagePath());

        //バリデーション
        if (bindingResult.hasErrors()) {

            model.addAttribute("image", user.imagePath());
            return "profile-edit";
        }

        String encode = null;

        //画像をエンコード　nullの場合は固定の画像をエンコード
        if (updateImage.isEmpty()) {
            File file = new File("src/main/resources/static/images/noImage/no-icon.png");
            byte[] bytes = Files.readAllBytes(file.toPath());
            encode = Base64.getEncoder().encodeToString(bytes);
        } else {
            byte[] bytes = updateImage.getBytes();
            encode = Base64.getEncoder().encodeToString(bytes);
        }

        //String loginId = editForm.getLoginId();
        String name = editForm.getName();
        String password = editForm.getPassword();
        kuchannelService.edit(loginId, name, password, encode);
        UserRecord userData = (UserRecord) session.getAttribute("user");
        var user_id = userData.loginId();

        var profile = kuchannelService.detail(user_id);

        //セッション情報を更新
        var newSession = kuchannelService.findUser(userData.id());
        session.setAttribute("user", newSession);

        model.addAttribute("profile", profile);
        model.addAttribute("image", newSession.imagePath());

        return "/profile-details";
    }

    @GetMapping("/backToThread")
    public String backToThread(@RequestParam(name = "communityId") Integer communityId) {


        var community = kuchannelService.findCommunity(communityId);
        String url = community.url();
        String moldedUrl = url.replace("http://localhost:8080/", "");


        return "redirect:/" + URLEncoder.encode(moldedUrl, StandardCharsets.UTF_8).replace("%2F", "/") + "/" + community.id() + "/threads";
    }

    //お問い合わせ対応完了処理
    @PostMapping("/kuchannel/inquiryComplete")
    public String completeInquiry(@RequestParam("id") Integer inquiryId) {

        //完了処理
        kuchannelService.completeInquiryUpdate(inquiryId);

        return "redirect:/kuchannel/notice";

    }

    @GetMapping("/iconCreate")  public String userIconCreate(@ModelAttribute("UserForm") UserForm userForm) throws IOException {

        iconCreate();
        return "login";
    }

    //プロフィールアイコンを自動生成する処理
    public void iconCreate() throws IOException {
        File directory = new File("src/main/resources/static/images/icons");

        File[] files = directory.listFiles();

        List<File> imageFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        kuchannelService.userImageCreate(imageFiles);
    }

//    @GetMapping("/threadImageCreate")
//    public String threadImageCreate(@ModelAttribute("UserForm") UserForm userForm) throws IOException {
//
//        threadCreate();
//        return "login";
//    }
//
//    //スレッド画像を自動生成する処理
//    public void threadCreate() throws IOException {
//        File directory = new File("src/main/resources/static/images/threadImages");
//
//        File[] files = directory.listFiles();
//
//        List<File> imageFiles = new ArrayList<>();
//        for (File file : files) {
//            if (file.isFile() && isImageFile(file)) {
//                imageFiles.add(file);
//            }
//        }
//
//        kuchannelService.threadImagesCreate(imageFiles);
//    }

}
