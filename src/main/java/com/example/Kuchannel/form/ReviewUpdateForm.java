package com.example.Kuchannel.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewUpdateForm {

    Integer id;
    String title;
    String content;
}
