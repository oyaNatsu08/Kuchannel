package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditForm {
//    int id;

    //private String loginId;
    //    @NotEmpty
    private String name;

    @NotEmpty
    private String password;


}

