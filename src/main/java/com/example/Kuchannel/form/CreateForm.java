package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateForm {
    private int id;

    @NotEmpty(message = "ユーザーIDは必須です")
    private String loginId;

    @NotEmpty(message = "名前は必須です")
    private String name;

    @NotEmpty(message = "パスワードは必須です")
    private String password;


    private String image_path;
}
