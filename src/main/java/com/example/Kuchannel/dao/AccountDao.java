package com.example.Kuchannel.dao;
import com.example.Kuchannel.record.*;


public interface AccountDao {

    //ログイン
    public UserRecord Login(String loginId, String password);

    //プロフィール画面
    public ProfileRecord detail(String loginId);

    //アカウント新規作成
    public CreateRecord create(String loginId, String password, String name, String image_path);
}
