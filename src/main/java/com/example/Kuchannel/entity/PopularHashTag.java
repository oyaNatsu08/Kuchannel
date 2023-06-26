package com.example.Kuchannel.entity;

import lombok.Data;

@Data
public class PopularHashTag {

    Integer id;
    String tagName;
    Integer count;

    public PopularHashTag(Integer id, String tagName, Integer count) {
        this.id = id;
        this.tagName = tagName;
        this.count = count;

        if(count == null){
            this.count = 0;
        }
    }
}
