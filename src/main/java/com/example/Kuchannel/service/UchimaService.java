package com.example.Kuchannel.service;


import com.example.Kuchannel.dao.UchimaDao;
import com.example.Kuchannel.entity.BelongingCommunities;
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



    //データベースから所属しているコミュニティを取得
    public List<BelongingCommunities> getBelongingCommunities(Integer userId){
        return uchimaDao.getBelongingCommunities(userId);

    }

}
