package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.BelongingCommunities;
import com.example.Kuchannel.entity.MyReview;
import com.example.Kuchannel.entity.MyThread;
import com.example.Kuchannel.entity.UserRecord;
import com.example.Kuchannel.service.UchimaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UchimaRestController {

    @Autowired
    UchimaService uchimaService;

    @Autowired
    HttpSession session;


    //所属しているコミュニティを取得。（マイページ用）
    @GetMapping("/getBelongingCommunities")
    public List<BelongingCommunities> getBelongingCommunities(){
        //System.out.println("ゲットコミュニティ");

        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        List<BelongingCommunities> communityList = uchimaService.getBelongingCommunities(user.id());
        return communityList;
    }

    //マイページで任意のコミュにティから退会する用の処理。
    @PostMapping(value = "/withdrawal", consumes = "text/plain;charset=UTF-8")
    public int withdrawal(@RequestBody String getCommunityId){
        var communityId =Integer.parseInt(getCommunityId);
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        int result = uchimaService.withdrawal(user.id(), communityId);
        return result;
    }

//    マイページ用の、ニックネームを変更する処理
    @PostMapping("/updateNickName")
    public int updateNickName(@RequestBody BelongingCommunities updateInfo){
        int result = uchimaService.updateNickName(updateInfo);
        return result;
    }

    //マイページ用で、スレッドを取得する
    @GetMapping("/getMyThreads")
    public List<MyThread> getMyThread(){
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している→修正済み
        UserRecord user = (UserRecord) session.getAttribute("user");
        List<MyThread> result = uchimaService.getMyThreads(user.id());
        return result;
    }

    //マイページ用で、レビューを取得する
    @GetMapping("/getMyReviews")
    public List<MyReview> getMyReviews(){
        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している → 修正済み
        var user = (UserRecord)session.getAttribute("user");
        List<MyReview> result = uchimaService.getMyReviews(user.id());
        return result;
    }

}
