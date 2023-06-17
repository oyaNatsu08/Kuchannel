package com.example.Kuchannel.entity;

import java.io.Serial;
import java.io.Serializable;

public record UserRecord(Integer id, String loginId, String name, String password, String imagePath) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
