package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KuchannelDao {

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

    /*-------------------------------------------*/

    //マイページ用で、所属しているコミュニティを取得する処理
    public List<BelongingCommunities> getBelongingCommunities(Integer userId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        var result = jdbcTemplate.query("SELECT c.id AS communityId ,c_u.user_id,c.name AS community_name,c_u.nick_name ,c_u.flag FROM community_user c_u JOIN communities c ON c_u.community_id = c.id WHERE c_u.user_id = :userId",param,new DataClassRowMapper<>(BelongingCommunities.class));
        System.out.println(result);
        return result;
    }

    //マイページ用で、コミュニティから退会する処理
    public int withdrawal(Integer userId,Integer communityId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("communityId", communityId);
        var result = jdbcTemplate.update("UPDATE community_user SET flag = false WHERE user_id = :userId AND community_id = :communityId",param);
        return result;
    }

    //マイページ用で、コミュニティのニックネームを変更する処理
    public int updateNickName(BelongingCommunities updateInfo){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("nickName",   updateInfo.getNickName());
        param.addValue("userId", updateInfo.getUserId());
        param.addValue("communityId", updateInfo.getCommunityId());
        var result = jdbcTemplate.update("UPDATE community_user SET nick_name = :nickName WHERE user_id = :userId AND community_id = :communityId",param);
        return result;
    }

    //    マイページ用で、自分が建てたスレッドを取得する処理
    public List<MyThread>getMyThreads(Integer userId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        var result = jdbcTemplate.query("SELECT th.id AS threadId, th.title AS threadTitle, co.name AS communityName, co.url AS communityUrl " +
                        "FROM threads th JOIN communities co ON th.community_id = co.id JOIN community_user cu ON co.id = cu.community_id " +
                        "WHERE th.user_id = :userId AND cu.user_id = :userId AND cu.flag = true;", param,
                new DataClassRowMapper<>(MyThread.class));
        System.out.println(result);
        return result;
    }

    //    マイページ用で、自分で書いたレビュー取得する処理
    public List<MyReview>getMyReviews(Integer userId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        var result = jdbcTemplate.query("SELECT rev.title AS reviewTitle, rev.review, th.title AS threadTitle, co.name AS communityName, rev.create_date AS createDate FROM reviews rev JOIN threads th ON rev.thread_id = th.id JOIN communities co ON th.community_id = co.id JOIN community_user cu on co.id = cu.community_id WHERE th.user_id = :userId AND cu.flag = true;",param,new DataClassRowMapper<>(MyReview.class));
        System.out.println(result);
        return result;
    }

    /*--------------------------------------------------------------*/

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

    //コミュニティに参加しているかチェック(ユーザーIDとurlで確認)
    public CommunityUserRecord checkJoin(Integer userId, String url) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("url", url);

        var list = jdbcTemplate.query("SELECT cu.id, user_id, community_id, nick_name, role, flag " +
                        "FROM community_user cu JOIN communities co ON cu.community_id = co.id " +
                        "WHERE cu.user_id = :userId AND co.url = :url AND cu.flag = true", param,
                new DataClassRowMapper<>(CommunityUserRecord.class));

        return list.isEmpty() ? null : list.get(0);

    }

    //コミュニティに参加しているかチェック(ユーザーIDとurlで確認)
    public CommunityUserRecord checkJoin(Integer userId, Integer communityId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("communityId", communityId);

        var list = jdbcTemplate.query("SELECT id, user_id, community_id, nick_name, role, flag FROM community_user " +
                        "WHERE user_id = :userId AND community_id = :communityId", param,
                new DataClassRowMapper<>(CommunityUserRecord.class));

        return list.isEmpty() ? null : list.get(0);

    }

    //コミュニティに再参加する処理
    public int communityUserUpdate(Integer userId, Integer communityId, String nickName) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("communityId", communityId);
        param.addValue("nickName", nickName);

        return jdbcTemplate.update("UPDATE community_user SET nick_name = :nickName, flag = 't' WHERE user_id = :userId AND community_id = :communityId", param);
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
