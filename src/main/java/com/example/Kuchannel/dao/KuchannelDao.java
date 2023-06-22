package com.example.Kuchannel.dao;

import com.example.Kuchannel.entity.*;
import com.example.Kuchannel.form.ThreadAddForm;
import jakarta.servlet.http.HttpSession;
import com.example.Kuchannel.entity.InformatonRecord;
//import com.example.Kuchannel.entity.ThreadRecord;
import com.example.Kuchannel.entity.InformatonRecord;
import com.example.Kuchannel.entity.ThreadRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class KuchannelDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private HttpSession session;

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
        var result = jdbcTemplate.query("SELECT th.id AS threadId, th.title AS threadTitle,co.id AS communityId, co.name AS communityName, co.url AS communityUrl " +
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
        var result = jdbcTemplate.query("SELECT rev.title AS reviewTitle, CONCAT(LEFT(rev.review,10),'...') AS review, " +
                "th.title AS threadTitle, co.name AS communityName, DATE(rev.create_date) AS createDate " +
                "FROM reviews rev JOIN threads th ON rev.thread_id = th.id " +
                "JOIN communities co ON th.community_id = co.id JOIN community_user cu on co.id = cu.community_id " +
                "WHERE th.user_id = :userId AND cu.user_id = :userId AND cu.flag = true;", param,
                new DataClassRowMapper<>(MyReview.class));
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

        var list = jdbcTemplate.query("SELECT inq.id, u.name AS inquiryUserName, co.id AS communityId, co.name AS communityName " +
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

        var list = jdbcTemplate.query("SELECT id, login_id, name, password, image_path " +
                        "FROM users WHERE id = :userId", param,
                new DataClassRowMapper<>(UserRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //コミュニティIDを元にコミュニティを特定する
    public CommunityRecord findCommunity(Integer communityId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("communityId", communityId);

        var list = jdbcTemplate.query("SELECT id, name, url, delete_date FROM communities WHERE id = :communityId", param,
                new DataClassRowMapper<>(CommunityRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //お知らせ詳細情報を取得する
    public InquiryDetailRecord findInquiry(Integer inquiryId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("inquiryId", inquiryId);

        var list = jdbcTemplate.query("SELECT id, user_id, community_id, content, flag FROM inquiries WHERE id = :inquiryId", param,
                new DataClassRowMapper<>(InquiryDetailRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //データベースからレビュー一覧に表示する情報を全件取得
    public List<ReviewRecord> findReviewAll(Integer threadId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("threadId", threadId);

        var list = jdbcTemplate.query("SELECT u.id AS userId, u.name AS userName, " +
                        "r.id AS reviewId, r.title, r.review, to_char(r.create_date, 'YYYY-MM-DD HH24:MI') AS createDate " +
                        "FROM users u JOIN reviews r ON u.id = r.user_id " +
                        "WHERE r.thread_id = :threadId ORDER BY r.id", param,
                        new DataClassRowMapper<>(ReviewRecord.class));

        return list;

    }

    //データベースからレビューの画像情報を取得する
    public List<ReviewImageRecord> getReviewImages(Integer reviewId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);

        var list = jdbcTemplate.query("SELECT review_id, image_path FROM review_images WHERE review_id = :reviewId",
                param, new DataClassRowMapper<>(ReviewImageRecord.class));

        return list;

    }

    //データベースからレビューの返信情報を取得する
    public List<ReviewReplyRecord> getReviewReply(Integer reviewId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);

        var list = jdbcTemplate.query("SELECT rep.id AS replyId, rep.user_id, u.name AS userName, rep.review_id, " +
                "rep.reply, to_char(rep.create_date, 'YYYY-MM-DD HH24:MI') AS createDate FROM replies rep JOIN users u ON rep.user_id = u.id " +
                "WHERE rep.review_id = :reviewId ORDER BY rep.id", param, new DataClassRowMapper<>(ReviewReplyRecord.class));

        return list;

    }

    //reviewsテーブルにインサート処理
    public int reviewInsert(int userId, int threadId, String title, String review) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("threadId", threadId);
        param.addValue("title", title);
        param.addValue("review", review);

        //インサートしたidを受け取るために必要
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO reviews(user_id, thread_id, title, review, create_date) " +
                "VALUES(:userId, :threadId, :title, :review, now())", param, keyHolder);

        //インサートしたIDを返す
        return Integer.parseInt(keyHolder.getKeys().get("id").toString());

    }

    //reviewsテーブルのレコードのupdate処理
    public int reviewUpdate(int reviewId, String title, String review) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", reviewId);
        param.addValue("title", title);
        param.addValue("review", review);

        var update = jdbcTemplate.update("UPDATE reviews SET title = :title, review = :review WHERE id = :userId",param);
        return update;
    }

    //review_Imagesテーブルにインサート処理
    public int reviewImagesInsert(int reviewId, String imagePath) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);
        param.addValue("imagePath", imagePath);

        return jdbcTemplate.update("INSERT INTO review_images(review_id, image_path) " +
                "VALUES(:reviewId, :imagePath)", param);
    }

    //repliesテーブルにインサート処理
    public ReviewReplyRecord replyInsert(int userId, int reviewId, String content) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);
        param.addValue("reviewId", reviewId);
        param.addValue("content", content);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO replies(user_id, review_id, reply, create_date) " +
                "VALUES(:userId, :reviewId, :content, now())", param, keyHolder);

        var user = (UserRecord)session.getAttribute("user");

        var reply = new ReviewReplyRecord(
                        Integer.parseInt(keyHolder.getKeys().get("id").toString()),
                        Integer.parseInt(keyHolder.getKeys().get("user_id").toString()),
                        user.name(),
                        Integer.parseInt(keyHolder.getKeys().get("review_id").toString()),
                        keyHolder.getKeys().get("reply").toString(),
                        keyHolder.getKeys().get("create_date").toString()
                );

        return reply;

    }

    //レビューIDを元にreviewsテーブルから情報を取得する
    public ReviewRecord findReview(Integer reviewId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);

        var list = jdbcTemplate.query("SELECT u.id AS userId, u.name AS userName, " +
                        "r.id AS reviewId, r.title, r.review, to_char(r.create_date, 'YYYY-MM-DD HH24:MI') AS createDate " +
                        "FROM users u JOIN reviews r ON u.id = r.user_id " +
                        "WHERE r.id = :reviewId", param,
                new DataClassRowMapper<>(ReviewRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    //ユーザーのレビュー一覧に必要な情報を取得する
    public List<UserReviewList> getUserReview(Integer userId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", userId);

        var list = jdbcTemplate.query("SELECT u.name AS userName, r.id AS reviewId, r.title AS reviewTitle, " +
                "r.review AS content, t.title AS threadTitle, c.name AS communityName, " +
                "DATE(r.create_date) AS createDate FROM reviews r JOIN users u ON r.user_id = u.id " +
                "JOIN threads t ON r.thread_id = t.id JOIN communities c ON t.community_id = c.id " +
                "WHERE r.user_id = :userId", param, new DataClassRowMapper<>(UserReviewList.class));

        return list;

    }

    //データベースからレビューのいいね件数を取得する
    public int getGoodReview(Integer reviewId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);

        var list = jdbcTemplate.query("SELECT COUNT(*) AS goodCount FROM review_goods WHERE review_id = :reviewId " +
                "GROUP BY review_id", param, new DataClassRowMapper<>(GoodCount.class));

        return list.isEmpty() ? 0 : list.get(0).getGoodCount();
    }

    /*---------------------------------------------*/

    //threadsテーブルにINSERTする処理
    public int threadInsert(ThreadAddForm threadAddForm) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("threadName", threadAddForm.getThreadName());
//        param.addValue("furigana", threadAddForm.getFurigana());
        param.addValue("address", threadAddForm.getAddress());
        param.addValue("salesTime", threadAddForm.getSalesTime());
        param.addValue("genre", threadAddForm.getGenre());
        param.addValue("hashTag",threadAddForm.getHashtag());

        KeyHolder keyHolder1 = new GeneratedKeyHolder();

        //user_idとコミュニティid今は1で固定。後で修正。
        var threadAddResult= jdbcTemplate.update("INSERT INTO threads(user_id, community_id, image_path, " +
                "title, address, sales_time, genre, create_date) VALUES(1, 1, null, :threadName, " +
                ":address, :salesTime, :genre, now())", param,keyHolder1);

        //インサートしたIDを受け取る
        var addThreadId = Integer.parseInt(keyHolder1.getKeys().get("id").toString());
        System.out.println(addThreadId);


        //以下ハッシュタグ追加処理


        if(threadAddForm.getHashtag() != null){
            //ハッシュタグの内容を取得し、「,」でsplitして分ける。
            String[] inputHashTags = threadAddForm.getHashtag().trim().split(",");

            //それぞれのデータについて、ハッシュタグテーブルに既にあるのかを検索。
            for(var i =0; i<inputHashTags.length; i++){
                param.addValue("tagName",inputHashTags[i]);
                List<HashTag> searchHashTagResult = jdbcTemplate.query("SELECT * FROM hashtags WHERE tag_name = :tagName;", param,
                        new DataClassRowMapper<>(HashTag.class));

                //もしすでに登録されていた場合、そのハッシュタグをスレッド情報に追加。
                if(!searchHashTagResult.isEmpty()){
                    System.out.println("ヌルじゃない"+searchHashTagResult.get(0));
                    var existingHashTagId =searchHashTagResult.get(0).getId();
                    param.addValue("hashTagId",existingHashTagId);
                    param.addValue("thread_id",addThreadId);
                    var hashTagInsertResult= jdbcTemplate.update("INSERT INTO thread_hashtag(thread_id,hashtag_id)VALUES(:thread_id,:hashTagId);",param);
                }

                //まだ登録されていなかったら、ハッシュタグテーブルに登録し、そのあとスレッドハッシュタグテーブルにも情報を追加
                else{
                    System.out.println("ヌルっぽい！");
                    KeyHolder keyHolder2 = new GeneratedKeyHolder();

                    var hashTagInsertResult= jdbcTemplate.update("INSERT INTO hashtags(tag_name)VALUES(:tagName);",param,keyHolder2);
                    var addHashTagId = Integer.parseInt(keyHolder2.getKeys().get("id").toString());

                    param.addValue("add_hashtag_id",addHashTagId);
                    param.addValue("thread_id",addThreadId);
                    var insertThreadHashTagResult= jdbcTemplate.update("INSERT INTO thread_hashtag(thread_id,hashtag_id)VALUES(:thread_id,:add_hashtag_id);",param);

                }

            }
        }

        return threadAddResult;
    }


    public int threadUpdate(ThreadAddForm inputData,Integer thread_id) {
        //ハッシュタグ以外のアップデート処理
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("thread_id", thread_id);
        param.addValue("title", inputData.getThreadName());
//        param.addValue("furigana",inputData.getFurigana());
        param.addValue("address", inputData.getAddress());
        param.addValue("sales_time", inputData.getSalesTime());
        param.addValue("genre", inputData.getGenre());
        param.addValue("hashtags", inputData.getHashtag());

        var updateThreadResult = jdbcTemplate.update("UPDATE threads SET title=:title,address=:address,sales_time=:sales_time,genre=:genre WHERE id = :thread_id", param);

        //ハッシュタグの処理。一度スレッドハッシュタグをリセットして、再度インサートする。
        var resetThreadHashtag = jdbcTemplate.update("DELETE FROM thread_hashtag WHERE thread_id = :thread_id", param);

        if (inputData.getHashtag() != null) {
            //ハッシュタグの内容を取得し、「,」でsplitして分ける。
            String[] inputHashTags = inputData.getHashtag().trim().split(",");

            //それぞれのデータについて、ハッシュタグテーブルに既にあるのかを検索。
            for (var i = 0; i < inputHashTags.length; i++) {
                param.addValue("tagName", inputHashTags[i]);
                List<HashTag> searchHashTagResult = jdbcTemplate.query("SELECT * FROM hashtags WHERE tag_name = :tagName;", param,
                        new DataClassRowMapper<>(HashTag.class));

                //もしすでに登録されていた場合、そのハッシュタグをスレッド情報に追加。
                if (!searchHashTagResult.isEmpty()) {
                    System.out.println("ヌルじゃない" + searchHashTagResult.get(0));
                    var existingHashTagId = searchHashTagResult.get(0).getId();
                    param.addValue("hashTagId", existingHashTagId);
                    var hashTagInsertResult = jdbcTemplate.update("INSERT INTO thread_hashtag(thread_id,hashtag_id)VALUES(:thread_id,:hashTagId);", param);
                }

                //まだ登録されていなかったら、ハッシュタグテーブルに登録し、そのあとスレッドハッシュタグテーブルにも情報を追加
                else {
                    System.out.println("ヌルっぽい！");
                    KeyHolder keyHolder = new GeneratedKeyHolder();

                    var hashTagInsertResult = jdbcTemplate.update("INSERT INTO hashtags(tag_name)VALUES(:tagName);", param, keyHolder);
                    var addHashTagId = Integer.parseInt(keyHolder.getKeys().get("id").toString());

                    param.addValue("add_hashtag_id", addHashTagId);
                    var insertThreadHashTagResult = jdbcTemplate.update("INSERT INTO thread_hashtag(thread_id,hashtag_id)VALUES(:thread_id,:add_hashtag_id);", param);

                }
            }
        }
        return resetThreadHashtag;
    }



    //コミュニティIDを元にスレッドを全件取得
    public List<CommunityThread> communityThreads(Integer communityId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", communityId);

        var list = jdbcTemplate.query("select *\n" +
                        "from threads t\n" +
                        "left join (\n" +
                        "  select thread_id, count(*) good_count\n" +
                        "  from thread_goods\n" +
                        "  group by thread_id) g\n" +
                        "  on t.id = g.thread_id\n" +
                        "\n" +
                        "left join \n" +
                        "(\n" +
                        "  select thread_id, string_agg(tag_name, ',') AS hashtags\n" +
                        "  from hashtags h\n" +
                        "  join thread_hashtag th\n" +
                        "  on h.id = th.hashtag_id\n" +
                        "  group by thread_id\n" +
                        ") h\n" +
                        "on h.thread_id = t.id\n" +
                        "where t.community_id = :id ORDER BY t.id", param,
                new DataClassRowMapper<>(CommunityThread.class));
        System.out.println(list);

        return list;
    }

    //スレッドIDをもとに、スレッド情報を取得する
    public CommunityThread getThread(Integer threadId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("threadId", threadId);

        var list = jdbcTemplate.query("select *\n" +
                        "from threads t\n" +
                        "left join (\n" +
                        "  select thread_id, count(*) good_count\n" +
                        "  from thread_goods\n" +
                        "  group by thread_id) g\n" +
                        "  on t.id = g.thread_id\n" +
                        "\n" +
                        "left join \n" +
                        "(\n" +
                        "  select thread_id, string_agg(tag_name, ',') AS hashtags\n" +
                        "  from hashtags h\n" +
                        "  join thread_hashtag th\n" +
                        "  on h.id = th.hashtag_id\n" +
                        "  group by thread_id\n" +
                        ") h\n" +
                        "on h.thread_id = t.id\n" +
                        "where t.id = :threadId ORDER BY t.id", param,
                new DataClassRowMapper<>(CommunityThread.class));

        return list.isEmpty() ? null : list.get(0);

    }

    /*------------------------------------*/

    //プロフィール編集（start）
    public ProfileEditRecord edit(String loginId, String name, String password) {
        //更新のSQL文
        String sql = "UPDATE users SET name = :name, password = :password WHERE login_id = :loginId";

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

    //お問い合わせ
    public int information(InformatonRecord informatonRecord){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", informatonRecord.userId());
        param.addValue("communityId", informatonRecord.communityId());
        param.addValue("content", informatonRecord.content());
        param.addValue("flag", informatonRecord.flag());

        var result = jdbcTemplate.update("INSERT INTO inquiries (user_id,community_id,content,flag) VALUES(:userId,:communityId,:content,:flag);",
                param);

        return result;
    }
    //言い値ボタンが押されたとき、そのユーザーがそのスレッドへいいねを押していない場合インサート、すでに押している場合は削除。そののちにその時のいいね数をCOUNTして数字で返したい。
    public int goodDealThread(Integer thread_id,Integer user_id){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("thread_id",thread_id);
        param.addValue("user_id",user_id);
        //すでにいいねされているかを検索。
        List<Good> searchGoodResult = jdbcTemplate.query("SELECT id, user_id, thread_id AS thread_review_id FROM thread_goods WHERE user_id=:user_id AND thread_id =:thread_id;", param,
                new DataClassRowMapper<>(Good.class));
        //そのデータがが存在する場合
        if(!searchGoodResult.isEmpty()){
            //データが存在する場合、searchGoodResultからidを取得し、それを消す。
            System.out.println("ヌルじゃない"+searchGoodResult.get(0));
            var existingGoodId =searchGoodResult.get(0).getId();
            param.addValue("good_id",existingGoodId);
            var deleteGoodResult= jdbcTemplate.update("DELETE FROM thread_goods WHERE id = :good_id;",param);
            return deleteGoodResult;
        }else{
            //データが存在しない場合、threadGoodテーブルにインサート。
            System.out.println("ヌルと思う");
            var deleteGoodResult= jdbcTemplate.update("INSERT INTO thread_goods(user_id,thread_id)VALUES(:user_id,:thread_id);",param);
            return deleteGoodResult;
        }

    }

    //レビューのいいね
    public int goodDealReview(Integer reviewId,Integer userId){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("reviewId", reviewId);
        param.addValue("userId", userId);
        //すでにいいねされているかを検索。
        List<Good> searchGoodResult = jdbcTemplate.query("SELECT id, user_id, review_id AS thread_review_id FROM review_goods " +
                        "WHERE user_id = :userId AND review_id = :reviewId", param,
                new DataClassRowMapper<>(Good.class));
        //そのデータがが存在する場合
        if(!searchGoodResult.isEmpty()){
            //データが存在する場合、searchGoodResultからidを取得し、それを消す。
            var existingGoodId = searchGoodResult.get(0).getId();
            param.addValue("good_id",existingGoodId);
            var deleteGoodResult= jdbcTemplate.update("DELETE FROM review_goods WHERE id = :good_id", param);
            return deleteGoodResult;
        }else{
            //データが存在しない場合、テーブルにインサート。
            var deleteGoodResult= jdbcTemplate.update("INSERT INTO review_goods(user_id, review_id) " +
                    "VALUES (:userId, :reviewId);", param);
            return deleteGoodResult;
        }

    }

    public boolean deleteThread(Integer thread_id){
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("thread_id",thread_id);
        List<MyReview> searchReviewResult = jdbcTemplate.query("SELECT title FROM reviews WHERE thread_id =:thread_id;", param,
                new DataClassRowMapper<>(MyReview.class));
        //レビューがないなら消してtrueを返す。
        if(searchReviewResult.isEmpty()){
            var deleteThreadHashtagResult = jdbcTemplate.update("DELETE FROM thread_hashtag WHERE thread_id = :thread_id;",param);
            var deleteThreadGoodResult = jdbcTemplate.update("DELETE FROM thread_goods WHERE thread_id = :thread_id;",param);
            var deleteThreadResult= jdbcTemplate.update("DELETE FROM threads WHERE id = :thread_id;",param);
            return true;
        }
        //レビューがあったら消さずにfalseを返す。
        return false;

    }

}
