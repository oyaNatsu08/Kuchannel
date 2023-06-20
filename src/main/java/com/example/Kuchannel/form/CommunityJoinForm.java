package com.example.Kuchannel.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CommunityJoinForm {

    @Length(max = 50)
    private String joinNickName;

    public CommunityJoinForm(){

    }

}
