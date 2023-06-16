package com.example.Kuchannel.service;

import com.example.Kuchannel.record.*;
import com.example.Kuchannel.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;

    //ログイン
    @Override
    public UserRecord Login(String loginId, String password) {
        return accountDao.Login(loginId, password);
    }

    //アカウント新規作成
    @Override
    public CreateRecord create(String loginId, String password, String name, String image_path) {
        return accountDao.create(loginId,password,name,image_path);
    }

    //プロフィール編集
    @Override
    public  ProfileEditRecord edit(int id,String name , String password){
        return accountDao.edit(name,password);
    }
}
