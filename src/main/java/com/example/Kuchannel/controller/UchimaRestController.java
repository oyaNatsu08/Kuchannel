package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.BelongingCommunities;
import com.example.Kuchannel.service.UchimaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
        System.out.println("ゲットコミュニティ");

        //session.getAttribute("userId");
        //セッションからユーザーのidを渡すように変える。今は1で固定している
        List<BelongingCommunities> communityList = uchimaService.getBelongingCommunities(1);
        return communityList;
    }
}
