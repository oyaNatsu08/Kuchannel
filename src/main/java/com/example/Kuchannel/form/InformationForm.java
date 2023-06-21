package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

//お問い合わせ

@Data
public class InformationForm {
    @NotEmpty
    String information;
}
