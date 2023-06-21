package com.example.Kuchannel.controller;

import com.example.Kuchannel.form.CreateForm;
import com.example.Kuchannel.form.EditForm;
import com.example.Kuchannel.form.UserForm;
import com.example.Kuchannel.record.CreateRecord;
import com.example.Kuchannel.record.ProfileEditRecord;
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
import org.springframework.web.bind.annotation.RequestParam;

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
        System.out.println("login");
        //バリデーション
        if (bindingResult.hasErrors()) {
            System.out.println("エラー");

            return "login";
        }

        String loginId = userForm.getLoginId();
        String password = userForm.getPassword();

        var user = accountService.Login(loginId, password);

        if (user == null) {
            model.addAttribute("error", "IDまたはパスワードが不正です");
            System.out.println("エラーヌル");

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
        return "/login";
    }
    /*------------------------------------------------------------------------*/


//    /*-------------------------Insert(新規追加)--------------------------*/
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
        accountService.create(loginId, password, name, image_path);
        return "redirect:/login";
    }
//    /*------------------------------------------------------------------------*/

//    /*-------------------------Update(プロフィール編集)--------------------------*/
    @GetMapping("/profile-edit")
    public String profileUpdate(@ModelAttribute("EditForm")EditForm editForm){
        return "profile-edit";
    }

    @PostMapping("profile-edit")
    public String profileUpdate(@Validated @ModelAttribute("EditForm") EditForm editForm,
                                BindingResult bindingResult,
                                Model model){
        //バリデーション
        if(bindingResult.hasErrors()){
            return "profile-edit";
        }
        String loginId = editForm.getLoginId();
        String name = editForm.getName();
        String password = editForm.getPassword();
        ProfileEditRecord edit = new ProfileEditRecord(loginId, name, password);
        accountService.edit(loginId, name, password);
        System.out.println("aaaaaaa");
        return "/profile-details";
    }
//    /*------------------------------------------------------------------------*/









}



