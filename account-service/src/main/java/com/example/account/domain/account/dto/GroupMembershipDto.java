package com.example.account.domain.account.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupMembershipDto extends GroupDto{
    @Data
    static public class MembershipDto{
        private Long id;
        private Long userId;
        private Long groupId;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String imageUrl;
    }
    private List<MembershipDto> membershipList;
}
