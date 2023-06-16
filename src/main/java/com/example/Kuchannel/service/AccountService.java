package com.example.Kuchannel.service;

import com.example.Kuchannel.record.*;


public interface AccountService {

    //ログイン
    public UserRecord Login(String loginId, String password);

    //アカウント新規作成
    public CreateRecord create(String loginId, String password, String name, String image_path);

    //プロフィール編集
    public ProfileEditRecord edit(int id,String name ,String password);

}
