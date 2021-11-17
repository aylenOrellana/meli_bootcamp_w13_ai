package com.bootcamp.SocialMeli.dtoBAD;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFollowersCountDTO {
    private int userId;
    private String userName;
    private int followersCount;

    public UserFollowersCountDTO(int userId, String userName, int followersCount) {
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount;
    }
}