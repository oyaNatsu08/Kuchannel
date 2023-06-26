package com.example.Kuchannel.entity;

public record NoticeReplyRecord(Integer replyUserId, Integer threadId, String threadTitle, Integer noticeId, Boolean flag, Integer reviewId) {
}
