package com.example.Kuchannel.service;


import com.example.Kuchannel.dao.UchimaDao;
import com.example.Kuchannel.entity.BelongingCommunities;
import com.example.Kuchannel.entity.MyThread;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UchimaService {

    @Autowired
    private UchimaDao uchimaDao;



    //マイページ用。データベースから所属しているコミュニティを取得
    public List<BelongingCommunities> getBelongingCommunities(Integer userId){
        return uchimaDao.getBelongingCommunities(userId);

    }

    //マイページ用。退会処理。
    public int withdrawal(Integer userId,Integer communityId){
        return uchimaDao.withdrawal(userId,communityId);

    }
    //マイページ用。ニックネームの変更
    public int updateNickName(BelongingCommunities updateInfo){
        return  uchimaDao.updateNickName(updateInfo);
    }

    //マイページで自分の立てたスレッドを表示する用
    public List<MyThread> getMyThreads(Integer userId){return uchimaDao.getMyThreads(userId);}

}
