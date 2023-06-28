package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import org.springframework.web.multipart.MultipartFile;

@Data
public class EditForm {
//    int id;

    //private String loginId;
    @NotEmpty(message="名前は必須です")
    @Length(max = 50,message = "1から50の間の長さにしてください")

    private String name;

    @NotEmpty(message = "パスワードは必須です")
    @Length(max = 50,message = "1から50の間の長さにしてください")
    private String password;


}

