package com.example.Kuchannel.dao;
import com.example.Kuchannel.entity.CreateRecord;
import com.example.Kuchannel.entity.ProfileRecord;
import com.example.Kuchannel.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Repository
public class PgAccountDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    //ログイン
    public UserRecord Login(String loginId, String password) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("loginId", loginId);
        parameterSource.addValue("password", password);
//        System.out.println(loginId);
//        System.out.println(password);

        var list = jdbcTemplate.query("SELECT * FROM users WHERE login_id = :loginId AND password = :password",
                parameterSource, new DataClassRowMapper<>(UserRecord.class));
        return list.isEmpty() ? null : list.get(0);
    }

    //プロフィール画面
    public ProfileRecord detail(String loginId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("loginId",loginId);

        var list = jdbcTemplate.query("SELECT * FROM users WHERE login_id = :loginId",
                parameterSource, new DataClassRowMapper<>(ProfileRecord.class));
        return list.get(0);
    }


    //アカウント新規作成（start）
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
//            int key = keyHolder.getKey().intValue();
            int key = Integer.parseInt(keyHolder.getKeys().get("id").toString());

            CreateRecord create = new CreateRecord(loginId, password, name, image_path);
            return create;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    //新規作成（end）

    //プロフィール編集（start）
//    public ProfileEditRecord edit(int id,String name, String password) {
//        //更新のSQL文
//        String sql = "UPDATE users SET name =:name, password = :password WHERE id = :id";
//
//        MapSqlParameterSource param = new MapSqlParameterSource();
//        param.addValue("name", name);
//        param.addValue("password", password);
//        param.addValue("id",id);
//
//    }
}