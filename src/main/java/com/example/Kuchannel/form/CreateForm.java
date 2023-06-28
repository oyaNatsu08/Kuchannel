package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateForm {
    private int id;

    @NotEmpty
    @Length(max = 50,message = "1から50の間の長さにしてください")
    private String loginId;

    @NotEmpty
    @Length(max = 50,message = "1から50の間の長さにしてください")
    private String name;

    @NotEmpty
    @Length(max = 50,message = "1から50の間の長さにしてください")
    private String password;


    private String image_path;
}
