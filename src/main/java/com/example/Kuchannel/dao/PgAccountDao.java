package com.example.Kuchannel.dao;
import com.example.Kuchannel.record.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.lang.model.element.ModuleElement;

@Repository
public class PgAccountDao implements AccountDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    //ログイン
    @Override
    public UserRecord Login(String loginId, String password) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("loginId", loginId);
        parameterSource.addValue("password", password);
        System.out.println(loginId);
        System.out.println(password);

        var list = jdbcTemplate.query("SELECT * FROM users WHERE login_id = :loginId AND password = :password",
                parameterSource, new DataClassRowMapper<>(UserRecord.class));
        return list.isEmpty() ? null : list.get(0);
    }


    //アカウント新規作成（start）
    @Override
    public CreateRecord create(String loginId, String password, String name, String image_path) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("loginId", loginId);
        param.addValue("password", password);
        param.addValue("name", name);
        param.addValue("image_path", image_path);

        String sql = "INSERT INTO users(login_id, password, name, image_path) VALUES(:loginId, :password, :name, :image_path)";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, param, keyHolder);
            int key = keyHolder.getKey().intValue();

            CreateRecord create = new CreateRecord(loginId, password, name, image_path);
            return create;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    //新規作成（end）

    //プロフィール編集（start）
    @Override
    public ProfileEditRecord edit(String loginId, String name, String password) {
        //更新のSQL文
        String sql = "UPDATE users SET name =:name, password = :password WHERE login_id = :loginId";

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("name", name);
        System.out.println(name);
        param.addValue("password", password);
        System.out.println(password);
        param.addValue("loginId",loginId);
        System.out.println(loginId);
        try {
            jdbcTemplate.update(sql, param);

            ProfileEditRecord editRecord = new ProfileEditRecord(loginId,name, password);
            return editRecord;
        }catch (DataAccessException  e){
            e.printStackTrace();
            return null;
        }
    }



}