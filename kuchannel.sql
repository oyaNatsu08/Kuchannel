--データベース作成
CREATE DATABASE kuchannel_db;

--テーブル作成

CREATE TABLE SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BYTEA NOT NULL,
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);


--各テーブルの全件検索
SELECT * FROM users;

SELECT * FROM communities;

SELECT * FROM community_user;

SELECT * FROM threads;

SELECT * FROM hashtags;

SELECT * FROM thread_hashtag;

SELECT * FROM reviews;

SELECT * FROM replies;

SELECT * FROM notices;

SELECT * FROM review_images;

SELECT * FROM thread_goods;

SELECT * FROM review_goods;

SELECT * FROM inquiries;




--マイページでコミュニティ表示用
SELECT c.name AS community_name,c_u.nick_name
FROM comunity_user c_u
JOIN comunities c
ON c_u.comunity_id = c.id
WHERE c_u.user_id =1

SELECT c.name AS community_name,c_u.nick_name FROM comunity_user c_u JOIN comunities c ON c_u.comunity_id = c.id WHERE c_u.user_id =1





--DROP TABLE集
DROP TABLE inquiries;

DROP TABLE review_goods;

DROP TABLE thread_goods;

DROP TABLE review_images;

DROP TABLE notices;

DROP TABLE replies;

DROP TABLE reviews;

DROP TABLE thread_hashtag;

DROP TABLE hashtags;

DROP TABLE threads;

DROP TABLE comunity_user;

DROP TABLE comunities;

DROP TABLE users;

CREATE TABLE users(
id SERIAL PRIMARY KEY,
login_id VARCHAR(50) NOT NULL UNIQUE,
name VARCHAR(50) NOT NULL,
password VARCHAR(50) NOT NULL,
image_path VARCHAR(255)
);
--DROP TABLE users;

--コミュニティテーブル
CREATE TABLE comunities(
id SERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL,
url VARCHAR(255) NOT NULL,
delete_date DATE
);
--DROP TABLE comunities;


CREATE TABLE comunity_user(
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
comunity_id INT NOT NULL REFERENCES comunities(id),
nick_name VARCHAR(50),
role INT NOT NULL,
flag BOOLEAN NOT NULL
);

--DROP TABLE comunity_user;

--スレッドテーブル
CREATE TABLE threads (
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
comunity_id INT NOT NULL REFERENCES comunities(id),
image_path VARCHAR(255),
title VARCHAR(50) NOT NULL,
address VARCHAR(255),
sales_time VARCHAR(25),
genre VARCHAR(25),
create_date TIMESTAMP NOT NULL
);
--DROP TABLE threads;

--ハッシュタグテーブル
CREATE TABLE hashtags (
id SERIAL PRIMARY KEY,
tag_name VARCHAR(50) NOT NULL
);
--DROP TABLE hashtags;

--スレッドハッシュタグテーブル
CREATE TABLE thread_hashtag (
id SERIAL PRIMARY KEY,
thread_id INT NOT NULL REFERENCES threads(id),
hashtag_id INT NOT NULL REFERENCES hashtags(id)
);
--DROP TABLE thread_hashtag;

--レビュー
CREATE TABLE reviews (
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
thread_id INT NOT NULL REFERENCES threads(id),
title VARCHAR(50),
review TEXT,
create_date TIMESTAMP NOT NULL
);
--DROP TABLE reviews;

CREATE TABLE replies(
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
review_id INT NOT NULL REFERENCES reviews(id),
reply TEXT NOT NULL,
create_date TIMESTAMP NOT NULL
);
--DROP TABLE replies;

CREATE TABLE notices(
id SERIAL PRIMARY KEY,
reply_id INT NOT NULL REFERENCES replies(id),
read_flag BOOLEAN NOT NULL
);
--DROP TABLE notices;

CREATE TABLE review_images(
id SERIAL PRIMARY KEY,
review_id INT NOT NULL REFERENCES reviews(id),
image_path VARCHAR(255)
);
--DROP TABLE review_images;

CREATE TABLE thread_goods(
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
thread_id INT NOT NULL REFERENCES threads(id)
);
--DROP TABLE thread_goods;

CREATE TABLE review_goods(
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
review_id INT NOT NULL REFERENCES reviews(id)
);
--DROP TABLE review_goods;

CREATE TABLE inquiries(
id SERIAL PRIMARY KEY,
user_id INT NOT NULL REFERENCES users(id),
comunity_id INT NOT NULL REFERENCES comunities(id),
content TEXT NOT NULL,
flag BOOLEAN NOT NULL
);











