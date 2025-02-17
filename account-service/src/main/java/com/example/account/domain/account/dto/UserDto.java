package com.example.account.domain.account.dto;

import com.example.account.core.dto.BaseUserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserDto extends BaseUserDto{

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long inviteGroupId;

    public void setPassword(String password){
        this.password = password;
    }

    public void setInviteGroupId(Long groupId){
        this.inviteGroupId = groupId;
    }

    public String toString(){
        return super.toString();
    }
}
