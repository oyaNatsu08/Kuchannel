package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.plaf.basic.BasicTreeUI;
import java.security.Key;
import java.util.List;

@Repository
public class OyafusoDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    //urlが重複しないかチェック
    public CommunityRecord checkUrl(String str, String name) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("url", str);
        param.addValue("name", name);
        var list = jdbcTemplate.query("SELECT id, name, url, delete_date FROM communities WHERE " +
                "url = CONCAT('http://localhost:8080/community/', :url, '/', :name)", param,
                new DataClassRowMapper<>(CommunityRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //コミュニティテーブルインサート処理
    public int communityInsert(String name, String url) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("name", name);
        param.addValue("url", url);

        //インサートしたidを受け取るために必要
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO communities(name, url) VALUES(:name, :url)", param, keyHolder);

        //インサートしたIDを返す
        return Integer.parseInt(keyHolder.getKeys().get("id").toString());

    }

    //コミュニティユーザーテーブルインサート処理
    public int communityUserInsert(Integer userId, Integer communityId, String nickName, Integer role) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("communityId", communityId);
        param.addValue("name", nickName);
        param.addValue("role", role);

        return jdbcTemplate.update("INSERT INTO community_user(user_id, community_id, nick_name, role, flag) " +
                "VALUES(:userId, :communityId, :name, :role, 't')", param);
    }

    //urlを取得する
    public UrlRecord getUrl(Integer id) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("communityId", id);
        var list = jdbcTemplate.query("SELECT url FROM communities WHERE id = :communityId", param,
                new DataClassRowMapper<>(UrlRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //URLを元にコミュニティIDを取得する
    public CommunityRecord getCommunity(String url) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("url", url);

        var list = jdbcTemplate.query("SELECT id, name, url, delete_date FROM communities WHERE url = :url", param,
                new DataClassRowMapper<>(CommunityRecord.class));

        return list.isEmpty() ? null : list.get(0);

    }

    //コミュニティに参加しているかチェック
    public CommunityUserRecord checkJoin(Integer userId, String url) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("url", url);

        var list = jdbcTemplate.query("SELECT cu.id, user_id, community_id, nick_name, role, flag " +
                        "FROM community_user cu JOIN communities co ON cu.community_id = co.id " +
                        "WHERE cu.user_id = :userId AND co.url = :url", param,
                        new DataClassRowMapper<>(CommunityUserRecord.class));

        return list.isEmpty() ? null : list.get(0);

    }

    //ユーザーIDをもとにレビューをセレクト
    public ReviewRecord findReviews(Integer reviewId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);
        var list = jdbcTemplate.query("SELECT id, user_id, thread_id, title, review, create_date FROM reviews WHERE id = :reviewId", param,
                new DataClassRowMapper<>(ReviewRecord.class));

        return list.isEmpty() ? null : list.get(0);

    }

    //ユーザーのお知らせ一覧をセレクト
    public List<NoticeReplyRecord> userNotice(Integer userId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);

        var list = jdbcTemplate.query("SELECT rep.user_id AS replyUserId, th.title AS threadTitle, " +
                        "no.read_flag AS flag, rev.id AS reviewId " +
                "FROM threads th JOIN reviews rev ON th.id = rev.thread_id " +
                "JOIN replies rep ON rev.id = rep.review_id " +
                "JOIN notices no ON rep.id = no.reply_id " +
                "WHERE rev.user_id = :userId", param,
                new DataClassRowMapper<>(NoticeReplyRecord.class));

        return list;

    }

    //ユーザーのお知らせ一覧(問い合わせ)をセレクト
    public List<InquiryRecord> userInquiry(Integer userId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);

        var list = jdbcTemplate.query("SELECT u.name AS inquiryUserName, co.id AS communityId, co.name AS communityName " +
                "FROM inquiries inq JOIN community_user cu ON inq.community_id = cu.community_id " +
                "JOIN communities co ON cu.community_id = co.id " +
                "JOIN users u ON inq.user_id = u.id " +
                "WHERE cu.role = 2 AND cu.user_id = :userId AND inq.flag = false", param,
                new DataClassRowMapper<>(InquiryRecord.class));

        return list;

    }


    //ユーザーIDを元に、ユーザーを特定する
    public UserRecord findUser(Integer userId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);

        var list = jdbcTemplate.query("SELECT id, login_id, name, password, image_path FROM users WHERE id = :userId", param,
                new DataClassRowMapper<>(UserRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

}
