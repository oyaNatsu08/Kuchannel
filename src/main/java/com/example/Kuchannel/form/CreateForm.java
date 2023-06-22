package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateForm {
    private int id;

    @NotEmpty
    private String loginId;

    //    @NotEmpty
    private String name;

    @NotEmpty
    private String password;


    private String image_path;
}
