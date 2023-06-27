package com.example.Kuchannel.form;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserForm {

    @NotEmpty(message = "※ユーザーIDは必須です")
    private String loginId;

    @NotEmpty(message = "※パスワードは必須です")
    private String password;
}
