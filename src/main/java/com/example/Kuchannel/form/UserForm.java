package com.example.Kuchannel.form;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserForm {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
