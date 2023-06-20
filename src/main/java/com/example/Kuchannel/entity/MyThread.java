package com.example.Kuchannel.entity;


import lombok.Data;

@Data
public class MyThread {
    private Integer threadId;
    private String threadTitle;
    private String communityName;
    private String communityUrl;

    public MyThread(){

    }

}
