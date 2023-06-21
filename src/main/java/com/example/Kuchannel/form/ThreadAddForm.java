package com.example.Kuchannel.form;

import lombok.Data;

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

}