package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EditForm {
//    int id;

    private String loginId;
    //    @NotEmpty
    private String name;

    @NotEmpty(message = "パスワードは必須です")
    private String password;

}

