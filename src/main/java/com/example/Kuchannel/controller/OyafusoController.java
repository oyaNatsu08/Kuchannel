package com.example.Kuchannel.controller;

import com.example.Kuchannel.form.CommunityAddForm;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    //任意のスレッド一覧にとぶ
    @GetMapping("/community/{randomString}/{code}")
    public String threadView(@PathVariable("randomString") String str,
                             @PathVariable("code") String code,
                             Model model) {
        model.addAttribute("code", code);
        return "thread-list";
    }

    //マイページ画面遷移
    @GetMapping("/my-page")
    public String myPageView() {
        return "my-page";
    }

    //お知らせ画面遷移
    @GetMapping("/notice")
    public String noticeView() {
        return "notice-list";
    }

}
