package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.BelongingCommunities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UchimaDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    public List<BelongingCommunities> getBelongingCommunities(Integer userId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        var result = jdbcTemplate.query("SELECT c.id AS communityId ,c.name AS community_name,c_u.nick_name ,c_u.flag FROM community_user c_u JOIN communities c ON c_u.community_id = c.id WHERE c_u.user_id = :userId",param,new DataClassRowMapper<>(BelongingCommunities.class));
        System.out.println(result);
        return result;
    }
}
