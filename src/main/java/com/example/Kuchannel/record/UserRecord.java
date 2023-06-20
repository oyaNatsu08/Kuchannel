package com.example.Kuchannel.record;

import java.io.Serializable;

public record UserRecord(Integer id, String loginId, String password) implements Serializable {
    private static final long serialVersionUID = 1L;
}
