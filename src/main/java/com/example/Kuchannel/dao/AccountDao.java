package com.example.Kuchannel.dao;
import com.example.Kuchannel.record.*;


public interface AccountDao {

    //ログイン
    public UserRecord Login(String loginId, String password);

    //アカウント新規作成
    public CreateRecord create(String loginId, String password, String name, String image_path);

    //プロフィール編集
    public ProfileEditRecord edit(String name , String password);


}
