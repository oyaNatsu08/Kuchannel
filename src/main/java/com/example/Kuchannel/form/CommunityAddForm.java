package com.example.Kuchannel.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CommunityAddForm {

    @NotEmpty(message = "コミュニティ名を入力してください")
    @Length(max = 50)
    String communityName;

    @Length(max = 50, message = "50文字以内で入力してください")
    String nickName;

}
