package com.example.socialmeli.controllers;

import com.example.socialmeli.exceptions.InvalidPromoException;
import com.example.socialmeli.exceptions.PostAlreadyExistException;
import com.example.socialmeli.exceptions.PostNotFoundException;
import com.example.socialmeli.exceptions.UserNotFoundException;
import com.example.socialmeli.dto.PostDTO;
import com.example.socialmeli.dto.response.CountPromosResponseDTO;
import com.example.socialmeli.dto.response.PostsResponseDTO;
import com.example.socialmeli.services.SocialMeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class PostsController {

    SocialMeliService service;

    public PostsController(SocialMeliService service) {
        this.service = service;
    }

    //US 0005
    @PostMapping("/post")
    public void loadPost(@RequestBody PostDTO newPost) throws PostAlreadyExistException, InvalidPromoException, UserNotFoundException {
        service.pushPost(newPost);
    }


    //US 0006
    @GetMapping("/followed/{userId}/list")
    public PostsResponseDTO getFollowedPostList(@PathVariable Integer userId, @RequestParam @Nullable String order) throws UserNotFoundException {
        return service.getFollowedPostList(userId, order);
    }

    //US 0010
    @PostMapping("/promo-post")
    public void loadPromopost(@RequestBody PostDTO newPost) throws PostAlreadyExistException, InvalidPromoException, UserNotFoundException {
        if (!newPost.isHasPromo() || newPost.getDiscount() <= 0.0) {
            throw new InvalidPromoException(newPost.getIdPost());
        }
        service.pushPost(newPost);
    }

    //US 0011
    @GetMapping("/{userId}/promo-post/count")
    public CountPromosResponseDTO getPromoCount(@PathVariable Integer userId) throws UserNotFoundException {
        return service.getPromoCount(userId);
    }

    //US 0012
    @GetMapping("/{userId}/list")
    public PostsResponseDTO getPromoPosts(@PathVariable Integer userId) throws UserNotFoundException {
        return service.getPromoPosts(userId);
    }

    /*

    //US 0015 - Agregado
    @GetMapping("/products")
    public List<PostDTO> getAll() {
        return service.getAllPosts();
    }

    //US 0019 - Agregado
    @PostMapping("/products/delete/{postId}")
    public void deletePostById(@PathVariable Integer postId) throws PostNotFoundException {
        service.deletePostById(postId);
    }

    //US 0016 - Agregado
    @GetMapping("/products/{postId}")
    public PostDTO getPost(@PathVariable Integer postId) throws PostNotFoundException {
        return service.getPostById(postId);
    }



    //    >>> REQUESTS PARA PROMOS <<<


    @PostMapping("/products/deleteall/{userId}")
    public void deleteAllPostsUser(@PathVariable Integer userId) throws UserNotFoundException {
        service.deletePostByUserId(userId);
    }*/

}