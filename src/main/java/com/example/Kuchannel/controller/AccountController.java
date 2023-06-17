package com.example.Kuchannel.controller;

import com.example.Kuchannel.form.CommunityAddForm;
import com.example.Kuchannel.form.CreateForm;
import com.example.Kuchannel.form.UserForm;
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
                        @ModelAttribute("communityAdd") CommunityAddForm communityAddForm,
                        Model model) {
        //System.out.println("login");
        //バリデーション
        if (bindingResult.hasErrors()) {
            //System.out.println("エラー");

            return "login";
        }

        String loginId = userForm.getLoginId();
        String password = userForm.getPassword();

        var user = accountService.Login(loginId, password);

        if (user == null) {
            model.addAttribute("error", "IDまたはパスワードが不正です");
            //System.out.println("エラーヌル");

            return "login";
        }
        //セッション
        session.setAttribute("user", user);

        return "my-page";

    }
    /*------------------------------------------------------------------------*/


    /*------------------------logout(ログアウト)-----------------------------------*/
    @PostMapping("/logout")
    public String logout0(@ModelAttribute("UserForm") UserForm userForm) {
        session.invalidate();
        return "logout";
    }
    @GetMapping("/logout")
    public String logout(@ModelAttribute("UserForm") UserForm userForm) {
        session.invalidate();
        return "logout";
    }
//    @GetMapping("/test")
//    public String top(@ModelAttribute("UserForm") UserForm userForm) {
//        if (session.getAttribute("user") == null) {
//            return "login";
//        } else {
//            return "test";
//        }
//    }
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
}



