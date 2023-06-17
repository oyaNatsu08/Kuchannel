package com.example.Kuchannel.entity;

public record ReviewRecord(Integer id, Integer userId, Integer threadId, String title, String review, String createDate) {
}
