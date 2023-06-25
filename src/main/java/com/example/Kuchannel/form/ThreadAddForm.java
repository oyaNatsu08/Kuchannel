package com.example.Kuchannel.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data //自動的にgetterとsetterを生成してくれるなど
public class ThreadAddForm {
    //入力値をフィールドとして作る
    //入力値を受け取るため

    //スレッド名(店名)
    String threadName;

    //フリガナ
    String furigana;

    //お店の住所
    String address;

    //営業時間
    String salesTime;

    //ジャンル
    String genre;

    //ハッシュタグ
    String hashtag;

    Integer communityId;
    String integrateThreadId;

    String base64Image;

    public ThreadAddForm(String threadName, String furigana, String address, String salesTime, String genre, String hashtag, Integer communityId, String base64Image) {
        this.threadName = threadName;
        this.furigana = furigana;
        this.address = address;
        this.salesTime = salesTime;
        this.genre = genre;
        this.hashtag = hashtag;
        this.communityId = communityId;
        this.base64Image = base64Image;
    }
}
