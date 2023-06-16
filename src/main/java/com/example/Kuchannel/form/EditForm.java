package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EditForm {
    int id;
    //    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

}

