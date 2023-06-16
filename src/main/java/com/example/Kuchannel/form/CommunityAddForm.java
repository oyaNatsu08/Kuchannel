package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CommunityAddForm {

    @NotEmpty
    @Length(max = 50)
    String communityName;

    @Length(max = 50)
    String nickName;

}
