package com.bootcamp.SocialMeli.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCountFollowersDTO {
    private int userId;
    private String userName;
    private int followers_count;
}
