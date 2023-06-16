package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.CommunityAddForm;
import com.example.Kuchannel.form.CommunityJoinForm;
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
public class OyafusoController {

    @Autowired
    private KuchannelService oyafusoService;

    @Autowired
    private HttpSession session;

    @GetMapping("/community-add")
    public String commuAdd(@ModelAttribute("communityAdd") CommunityAddForm communityAddForm) {
        return "community-add";
    }

    //コミュニティ作成処理
    @PostMapping("/community-add")
    public String communityAdd(@Validated @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "community-add";
        } else {
            String inviteCode = communityAddForm.getCommunityName();

            String randomString;
            do {
                //ランダムな文字列を生成
                randomString = generateRandomString();
            } while (oyafusoService.checkUrl(randomString, communityAddForm.getCommunityName()) != null); //テーブルにurlが存在しなければループを抜ける ////urlが重複しないかチェック

            //コミュニティを作成する処理と作成したIDを受け取る
            int communityId = oyafusoService.communityInsert(communityAddForm.getCommunityName(), "http://localhost:8080/community/" + randomString + "/" + communityAddForm.getCommunityName());

            //コミュニティユーザーテーブルにインサートする処理
            oyafusoService.communityUserInsert(1, communityId, communityAddForm.getNickName(), 2); //Integer.parseInt(session.getAttribute("userId").toString())

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
                             @PathVariable("randomString") String str,
                             @PathVariable("code") String code,
                             Model model) {

        //ログインしているか確認
//        if (session.getAttribute("user") == null) {
//            return "login";
//        } else {
            //コミュニティに初めて参加する場合は、参加確認画面へ飛んで、community_userテーブルを更新
            //コミュニティに参加しているかチェック
            var communityUser = oyafusoService.checkJoin(3, "http://localhost:8080/community/" + str + "/" + code);
            if (communityUser == null) {
                model.addAttribute("communityName", code);
                model.addAttribute("url", "http://localhost:8080/community/" + str + "/" + code);
                //参加確認画面へ
                return "community-join";
            } else {
                model.addAttribute("code", code);
                return "thread-list";
            }
//        }
    }

    //マイページ画面遷移
    @GetMapping("/my-page")
    public String myPageView() {
        return "my-page";
    }

    //お知らせ画面遷移
    @GetMapping("/notice")
    public String noticeView(Model model) {

        //お知らせ一覧を表示する
        List<NoticeReplyRecord> notices = oyafusoService.userNotice(1);

        //お知らせ一覧のユーザーIDをもとに、返信ユーザーを取得する
        List<NoticeReplyRecord2> notices2 = new ArrayList<>();
        for (NoticeReplyRecord notice : notices) {
            UserRecord replyUser = oyafusoService.findUser(notice.replyUserId());
            notices2.add(new NoticeReplyRecord2(replyUser.name(), notice.threadTitle(), notice.flag(), notice.reviewId()));
        }

        //お問い合わせ情報を表示する
        List<InquiryRecord> inquires = oyafusoService.userInquiry(1);

        model.addAttribute("notices", notices2);
        model.addAttribute("inquires", inquires);

        return "notice-list";
    }

    //レビュー一覧へ(仮)
    @GetMapping("/review")
    public String reviewView(@RequestParam("id") Integer reviewId,
                             Model model) {

        ReviewRecord review = oyafusoService.findReviews(reviewId);

        model.addAttribute("review", review);

        return "review";
    }

    //コミュニティに参加する処理
    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("communityJoin") CommunityJoinForm communityForm,
                       BindingResult bindingResult,
                       @RequestParam ("url") String url,
                       Model model) {
        if (bindingResult.hasErrors()) {
            //参加したいコミュニティを特定する
            CommunityRecord community = oyafusoService.getCommunity(url);

            model.addAttribute("communityName", community.name());

            return "community-join";
        } else {

            //参加したいコミュニティを特定する
            CommunityRecord community = oyafusoService.getCommunity(url);

            //コミュニティユーザーテーブルにインサートする処理
            oyafusoService.communityUserInsert(3, community.id(), communityForm.getJoinNickName(), 1); //Integer.parseInt(session.getAttribute("userId").toString())

            model.addAttribute("code", community.name());

            return "thread-list";

        }
    }

}
