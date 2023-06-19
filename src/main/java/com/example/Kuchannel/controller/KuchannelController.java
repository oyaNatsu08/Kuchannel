package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.CommunityAddForm;
import com.example.Kuchannel.form.CommunityJoinForm;
import com.example.Kuchannel.form.CreateForm;
import com.example.Kuchannel.form.UserForm;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

            model.addAttribute("code", inviteCode);

            return "thread-list";
        }

    }

    //ランダムな文字列を生成するメソッド
    public static String generateRandomString() {
        UUID uuid = UUID.randomUUID();
        String randomString = uuid.toString().replace("-", "");
        return randomString;
    }

    //任意のスレッド一覧にとぶ(コミュニティへ飛ぶ)
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
                model.addAttribute("code", code);
                return "thread-list";
            }
        }
    }

    //お知らせ画面遷移
    @GetMapping("/notice")
    public String noticeView(@ModelAttribute("UserForm") UserForm userForm, Model model) {

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

    //レビュー一覧へ(仮)
    @GetMapping("/review")
    public String reviewView(@RequestParam("id") Integer reviewId,
                             @ModelAttribute("UserForm") UserForm userForm,
                             Model model) {
        //ログインしているか確認
        if (session.getAttribute("user") == null) {
            return "login";
        } else {

            ReviewRecord review = kuchannelService.findReviews(reviewId);

            model.addAttribute("review", review);

            return "review";
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

            model.addAttribute("code", community.name());

            return "thread-list";

        }
    }

}
