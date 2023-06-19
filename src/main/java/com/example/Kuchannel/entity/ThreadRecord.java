package com.example.Kuchannel.entity;

import java.time.LocalDate;

public record ThreadRecord(Integer id, Integer userId, Integer communityId, String imagePath,
                           String title, String address, String salesTime, String genre,
                           LocalDate createDate) {
}