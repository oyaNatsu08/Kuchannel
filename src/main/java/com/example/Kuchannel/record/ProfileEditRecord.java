package com.example.Kuchannel.record;

//プロフィール編集
public record ProfileEditRecord(String loginId,String name,String password) {
    public ProfileEditRecord(String loginId,String name ,String password) {
//        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
