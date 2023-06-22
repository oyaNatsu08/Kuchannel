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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                             Model model) {
        //バリデーション
        if (bindingResult.hasErrors()) {
            return "create-user";
        }
        String loginId = createForm.getLoginId();
        String password = createForm.getPassword();
        String name = createForm.getName();
        String image_path = createForm.getImage_path();
        CreateRecord create = new CreateRecord(loginId, password, name, image_path);
        kuchannelService.create(loginId, password, name, image_path);
        return "redirect:/login";
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

            model.addAttribute("name", inviteCode);

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
                model.addAttribute("name", code);
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
                notices2.add(new NoticeReplyRecord2(replyUser.name(), notice.threadTitle(), notice.flag(), notice.reviewId()));
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

            model.addAttribute("name", community.name());

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
    @GetMapping("/community/thread-list")
    public String threadListView(@RequestParam(name="id")Integer communityId,
                                 Model model) {
        //コミュニティIDを元にコミュニティを特定する
        CommunityRecord community = kuchannelService.findCommunity(communityId);

        model.addAttribute("name", community.name());

        return "thread-list";

    }

    //レビュー一覧へ飛ぶ
    @GetMapping("review-list")
    public String reviewListView(@ModelAttribute("UserForm") UserForm userForm) {

        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
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
                               Model model) {

        //レビューIDを元にreviewsテーブルから情報を取得する
        var review = kuchannelService.findReview(reviewId);

        //データベースからレビューの画像情報を取得する
        var reviewImages = kuchannelService.getReviewImages(reviewId);

        //データベースからレビューの返信情報を取得する
        var reviewReplies = kuchannelService.getReviewReply(reviewId);

        model.addAttribute("review", new ReviewElementAll(review.userId(), review.userName(), review.reviewId(), review.title(),
                review.review(), review.createDate(), reviewImages, reviewReplies));
        model.addAttribute("reviewId", reviewId);

        return "review-detail";

    }


    /*------------------------------------------------*/

    //スレッド作成画面へ遷移
    @GetMapping("/thread-add") //urlで入力する値
    public String threadAddView(@ModelAttribute("threadForm")ThreadAddForm threadAddForm,
                                @RequestParam(name="communityId") Integer communityId,
                                Model model) { //メソッド名(コントローラークラスの中で被らなければok)

        //コミュニティIDをthread-add.htmlに値を渡す
        model.addAttribute("communityId", communityId);

        return "thread-add"; //開きたいhtmlファイル名
    }

    //スレッド作成処理
    @PostMapping("/thread-add")
    public String addThread(@ModelAttribute("threadForm")ThreadAddForm threadAddForm,
                            Model model) {
        //threadsテーブルにINSERT処理
        kuchannelService.threadInsert(threadAddForm);

        //コミュニティIDを元にスレッドを全件取得(現在は1固定)
        var threads = kuchannelService.communityThreads(1);

        //thread-list.htmlにthreadsの値を渡す
        model.addAttribute("threads", threads);

        return "thread-list";
    }

/*-----------------------------------------------------------*/












/*-----------------------------------------------------------*/

}
