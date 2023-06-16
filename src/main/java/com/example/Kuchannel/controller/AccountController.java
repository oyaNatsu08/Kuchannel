package com.example.Kuchannel.controller;

import com.example.Kuchannel.form.CreateForm;
import com.example.Kuchannel.form.UserForm;
import com.example.Kuchannel.record.CreateRecord;
import com.example.Kuchannel.service.AccountServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @Autowired
    private HttpSession session;

    @Autowired
    AccountServiceImpl accountService;

    /*------------------------login(ログイン)-----------------------------------*/
    @GetMapping("/login")
    public String user(@ModelAttribute("UserForm") UserForm userForm) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("UserForm") UserForm userForm,
                        BindingResult bindingResult,
                        Model model) {
        //バリデーション
        if (bindingResult.hasErrors()) {
            return "login";
        }

        String loginId = userForm.getLoginId();
        String password = userForm.getPassword();

        var user = accountService.Login(loginId, password);

        if (user == null) {
            model.addAttribute("error", "IDまたはパスワードが不正です");
            return "login";
        }
        //セッション
        session.setAttribute("user", user);
        return "test";

    }
    /*------------------------------------------------------------------------*/


    /*------------------------logout(ログアウト)-----------------------------------*/
    @PostMapping("/logout")
    public String logout(@ModelAttribute("UserForm") UserForm userForm) {
        session.invalidate();
        return "logout";
    }
    @GetMapping("/test")
    public String top(@ModelAttribute("UserForm") UserForm userForm) {
        if (session.getAttribute("user") == null) {
            return "login";
        } else {
            return "test";
        }
    }
    /*------------------------------------------------------------------------*/


//    /*-------------------------Insert(新規追加)--------------------------*/
//    @GetMapping("/account-add")
//    public String newUser(@ModelAttribute("CreateForm") CreateForm createForm) {
//        return "account-add";
//    }
//
//    @GetMapping("/account-add")
//    public String accountAdd(@ModelAttribute("CreateForm") CreateForm createForm) {
//        return "account-add";
//    }
//
//    @PostMapping("account-add")
//    public String accountAdd(@Validated @ModelAttribute("CreateForm") CreateForm createForm,
//                         BindingResult bindingResult,
//                         Model model) {
//        //バリデーション
//        if (bindingResult.hasErrors()) {
//            return "account-add";
//        }
//        String loginId = createForm.getLoginId();
//        String password = createForm.getPassword();
//        String name = createForm.getName();
//        String image_path = createForm.getImage_path();
//        CreateRecord create = new CreateRecord(loginId, password, name, image_path);
//        accountService.create(loginId, password, name, image_path);
//        return "redirect:/menu";
//    }
//    /*------------------------------------------------------------------------*/
}



