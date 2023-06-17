package com.example.Kuchannel.controller;

import com.example.Kuchannel.form.CommunityAddForm;
import com.example.Kuchannel.form.UserForm;
import com.example.Kuchannel.service.UchimaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UchimaController {


//    @Autowired
//    UchimaService uchimaService;
//
    @Autowired
    HttpSession session;

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


}
