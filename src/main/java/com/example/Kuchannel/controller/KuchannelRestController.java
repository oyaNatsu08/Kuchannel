package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import com.example.Kuchannel.service.KuchannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    //スレッド削除用
    @DeleteMapping("/deleteThread/{threadId}")
    public boolean deleteThread(@PathVariable("threadId")Integer thread_id){
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
    public int addThread(@RequestBody ThreadAddForm inputData) {
        //threadsテーブルにINSERT処理
        var result =kuchannelService.threadInsert(inputData);
        return result;
    }

    @PutMapping("/updateThread/{id}")
    public int threadUpdate(@RequestBody ThreadAddForm inputData,@PathVariable("id") Integer thread_id){
        var result =kuchannelService.threadUpdate(inputData ,thread_id);
        return result;
    }

    @PutMapping("/memberSetting/{communityId}")
    public int memberSetting(@PathVariable("communityId") Integer communityId,@RequestBody List<AccountInformation> updateInfo){
        System.out.println(updateInfo);
        var result =kuchannelService.memberSetting(updateInfo, communityId);
        return result;
    }

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




}
