package com.example.Kuchannel.form;

import lombok.Data;

@Data
public class ProfileForm {
    private String name;
    private String loginId;
    private String password;
    private String imagePath;

    public ProfileForm(){}

}
