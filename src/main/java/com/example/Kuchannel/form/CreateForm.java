package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateForm {
    private int id;

    @NotEmpty(message = "ユーザーIDは必須です")
    @Length (min = 1, max = 50)
    private String loginId;

    @NotEmpty(message = "名前は必須です")
    @Length (min = 1, max = 50)
    private String name;

    @NotEmpty(message = "パスワードは必須です")
    @Length (min = 1, max = 50)
    private String password;


    private String image_path;
}
