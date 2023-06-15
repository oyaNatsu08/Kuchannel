package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CommunityAddForm {

    @NotNull
    @Length(max = 50)
    String communityName;

    @Length(max = 50)
    String nickName;

}
