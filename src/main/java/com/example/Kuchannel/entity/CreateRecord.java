package com.example.Kuchannel.entity;

public record CreateRecord(String loginId, String password, String name, String image_path) {
    public CreateRecord(String loginId, String password, String name, String image_path) {
//        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.image_path = image_path;
    }
}
