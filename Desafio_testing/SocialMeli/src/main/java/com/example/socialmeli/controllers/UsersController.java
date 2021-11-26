package com.example.socialmeli.controllers;

import com.example.socialmeli.exceptions.*;
import com.example.socialmeli.dto.response.*;
import com.example.socialmeli.dto.UserDTO;
import com.example.socialmeli.services.SocialMeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    SocialMeliService service;

    public UsersController(SocialMeliService service) {
        this.service = service;
    }

    //US 0001
    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public void followUser(@PathVariable Integer userId, @PathVariable Integer userIdToFollow) throws UserNotFoundException, UserAlreadyInUseException, UserSelfUseException {
        service.follow(userId, userIdToFollow);
    }

    //US 0002
    @GetMapping("/{userId}/followers/count")
    public CountFollowersResponseDTO countFollowers(@PathVariable Integer userId) throws UserNotFoundException {
        return service.countFollowers(userId);
    }

    //US 0003 - US 0008
    @GetMapping("/{userId}/followers/list")
    public FollowersResponseDTO getFollowers(@PathVariable Integer userId, @RequestParam @Nullable String order) throws UserNotFoundException {
        return this.service.getFollowers(userId, order);
    }

    //US 0004 - US 0009
    @GetMapping("/{userId}/followed/list")
    public FollowedResponseDTO getFollowed(@PathVariable Integer userId, @RequestParam @Nullable String order) throws UserNotFoundException {
        return this.service.getFollowed(userId, order);
    }

    //US 0007
    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public void unfollowUser(@PathVariable Integer userId, @PathVariable Integer userIdToUnfollow) throws UserNotFoundException, UserSelfUseException, UserAlreadyInUseException {
        service.unfollow(userId, userIdToUnfollow);
    }

}
