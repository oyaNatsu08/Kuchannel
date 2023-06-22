package com.example.Kuchannel.entity;

//プロフィール編集
public record ProfileEditRecord(String loginId,String name,String password) {
    public ProfileEditRecord(String loginId,String name ,String password) {
//        this.Id = Id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
