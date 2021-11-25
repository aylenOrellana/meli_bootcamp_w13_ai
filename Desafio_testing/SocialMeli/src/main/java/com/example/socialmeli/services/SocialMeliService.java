package com.example.socialmeli.services;

import com.example.socialmeli.exceptions.*;
import com.example.socialmeli.dto.response.*;
import com.example.socialmeli.dto.PostDTO;
import com.example.socialmeli.dto.UserDTO;
import com.example.socialmeli.model.Post;
import com.example.socialmeli.model.User;
import com.example.socialmeli.repositories.PostRepository;
import com.example.socialmeli.repositories.UsuarioRepository;
import com.example.socialmeli.utils.MiFactory;
import com.example.socialmeli.utils.Sorter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialMeliService implements IService {

    @Autowired
    UsuarioRepository userRepository;

    @Autowired
    PostRepository postRepository;

    ModelMapper mapper = new ModelMapper();

    /*public SocialMeliService(UsuarioRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }*/

    //  >>>> USER METHODS
    //!depracted
    public UserDTO getUserById(Integer id) throws UserNotFoundException {
        return mapper.map( userRepository.getById(id), UserDTO.class);
    }

    public void createUser(UserDTO newUser) throws UserAlreadyInUseException, UserNotFoundException {
        List<User> users = userRepository.getAll();

        if(users.stream().anyMatch( user -> user.getUserId().equals(newUser.getUserId()))){
            throw new UserAlreadyInUseException(newUser.getUserId());
        }

        for(Integer id : newUser.getFollowersId()){
            if( !users.stream().anyMatch( user -> user.getUserId().equals(id) )){
                throw new UserNotFoundException(id);
            }
        }

        userRepository.push(mapper.map(newUser, User.class));
    }

    public void deleteUserById(Integer userId) throws UserNotFoundException {
        deletePostByUserId(userId);
        userRepository.removeById(userId);
    }

    public void follow(Integer userId, Integer userToFollowId) throws UserNotFoundException, UserAlreadyInUseException, UserSelfUseException {
        if(userId.equals(userToFollowId)){
            throw new UserSelfUseException(userId);
        }

        User user = userRepository.getById(userId);
        User userToFollow = userRepository.getById(userToFollowId);

        if( userToFollow.getFollowersId().stream().anyMatch( id -> id.equals(userId) ) ){
            throw new UserAlreadyInUseException(userToFollowId);
        }

        userRepository.getById(userToFollowId).getFollowersId().add(userId);
    }

    public void unfollow(Integer userId , Integer userIdToUnfollow) throws UserNotFoundException, UserSelfUseException, UserAlreadyInUseException {
        if(userId.equals(userIdToUnfollow)){
            throw new UserSelfUseException(userId);
        }

        User user = userRepository.getById(userIdToUnfollow);

        if( !user.getFollowersId().stream().anyMatch( u -> u.equals(userId) ) ){
            throw new UserAlreadyInUseException(userIdToUnfollow);
        }

        userRepository.getById(userIdToUnfollow).getFollowersId().removeIf( id -> id.equals(userId));
    }

    public FollowersResponseDTO getFollowers(Integer userId, String order) throws UserNotFoundException {
        FollowersResponseDTO followers = new FollowersResponseDTO();

        User user = userRepository.getById(userId);

        followers.setFollowers(
                getFollowersList(userId,order).stream()
                        .collect(Collectors.toList()));

        followers.setUserId(userId);
        followers.setUserName(user.getUserName());

        return followers;
    }

    public FollowedResponseDTO getFollowed(Integer userId, String order) throws UserNotFoundException {
        FollowedResponseDTO followed = new FollowedResponseDTO();
        User user = userRepository.getById(userId);

        followed.setFollowed(getFollowedList(userId,order));
        followed.setUserName(user.getUserName());
        followed.setUserId(userId);

        return followed;
    }

    private List<UserDTO> getFollowersList(Integer userId, String order) throws UserNotFoundException {
        User user = userRepository.getById(userId);

        List<User> followersList = new ArrayList<>();

        for( Integer idFollower : user.getFollowersId() ){

            followersList.add( userRepository.getById(idFollower) );

        }

        Sorter sorter = MiFactory.getInstance(order == null ? "name_desc" : order);

        return followersList.stream()
                .map( u -> mapper.map(u, UserDTO.class))
                .sorted( (u,b) -> sorter.sort(u,b) )
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowedList(Integer userId, String order) throws UserNotFoundException {

        List<User> allUsers = userRepository.getAll();

        if( !userExists(userId) ){
            throw new UserNotFoundException(userId);
        }

        Sorter sorter = MiFactory.getInstance(order == null ? "name_desc" : order);

        return allUsers.stream().filter(user -> isFollowing(user,userId))
                .map( u -> mapper.map(u, UserDTO.class))
                .sorted( (u,b) -> sorter.sort(u,b))
                .collect(Collectors.toList());

    }

    private boolean isFollowing(User userFollower, Integer followId){
        return userFollower.getFollowersId().stream().anyMatch(id -> id.equals(followId));
    }

    public CountFollowersResponseDTO countFollowers(Integer id) throws UserNotFoundException {
        User user = userRepository.getById(id);
        CountFollowersResponseDTO quantity = new CountFollowersResponseDTO();
        quantity.setUserId(user.getUserId());
        quantity.setUserName(user.getUserName());
        quantity.setFollowersCount((int) user.getFollowersId().stream().count());

        return quantity;
    }

    private boolean userExists(Integer id){
        return userRepository.getAll().stream().anyMatch( user -> user.getUserId().equals(id) );
    }

    private boolean postExists(Integer id){
        return postRepository.getAll().stream()
                .anyMatch( post -> post.getIdPost().equals(id) );
    }

    //  >>>> POSTS METHODS

    public PostDTO getPostById(Integer postId) throws PostNotFoundException {

        return mapper.map(
                this.postRepository.getById(postId),
                PostDTO.class
        );
    }

    public void pushPost(PostDTO newPost) throws PostAlreadyExistException, InvalidPromoException, UserNotFoundException {

        if( postExists(newPost.getIdPost()) ){
            throw new PostAlreadyExistException(newPost.getIdPost(), newPost.getUserId());
        }

        if( !userExists(newPost.getUserId()) ){
            throw new UserNotFoundException(newPost.getUserId());
        }

        if(newPost.getDiscount() > 1){
            throw new InvalidPromoException(newPost.getIdPost());
        }

        this.postRepository.push(
                mapper.map( newPost, Post.class) );
    }

    public void deletePostById(Integer id) throws PostNotFoundException {
        if( !postRepository.getAll().stream().anyMatch(post -> post.getIdPost().equals(id)) ){
            throw new PostNotFoundException(id);
        }
        postRepository.removeById(id);
    }

    public void deletePostByUserId(Integer userId) throws UserNotFoundException {

        if(!userExists(userId)){ throw new UserNotFoundException(userId);}

        postRepository.getAll().removeIf(post -> post.getUserId().equals(userId));
    }

    private List<PostDTO> getUserPosts(Integer id){
        return this.postRepository.getAll().stream()
                .filter(post -> post.getUserId().equals(id))
                .map( post -> mapper.map( post, PostDTO.class ) )
                .collect(Collectors.toList());
    }

    public PostsResponseDTO getFollowedPostList(Integer id, String order) throws UserNotFoundException {
            Sorter sorter = MiFactory.getInstance(order == null ? "date_desc" : order );

            PostsResponseDTO response = new PostsResponseDTO();

            List<PostDTO> postsList = getFollowedList(id,null).stream().
                    flatMap(user -> getUserPosts(user.getUserId()).stream()).
                    sorted( (u,b) -> sorter.sort(u,b) ).
                    filter(post -> post.getDate().compareTo(Date.from(LocalDate.now().minusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant())) >0).
                    collect(Collectors.toList());

            response.setUserId(id);
            response.setPosts(postsList);

            return response;
    }


    public PostsResponseDTO getUserPostRequest(Integer id) throws UserNotFoundException {
        User user = userRepository.getById(id);

        PostsResponseDTO response = new PostsResponseDTO();

        response.setUserId(user.getUserId());
        response.setPosts( getUserPosts(user.getUserId()));

        return response;
    }

    public List<PostDTO> getAllPosts(){
        return postRepository.getAll().stream()
                .map(post -> mapper.map(post, PostDTO.class) )
                .collect(Collectors.toList());
    }

    public CountPromosResponseDTO getPromoCount(Integer userId) throws UserNotFoundException {
        CountPromosResponseDTO response = new CountPromosResponseDTO();

        User user = userRepository.getById(userId);

        Integer cantidad = Math.
                toIntExact(postRepository.getAll().stream().filter(post ->
                    post.getUserId().equals(userId) && post.isHasPromo()
                ).count());

        response.setUserName(user.getUserName());
        response.setUserId(userId);
        response.setPromoproductsCount(cantidad);

        return response;
    }

    public PostsResponseDTO getPromoPosts(Integer userId) throws UserNotFoundException {
        User user = userRepository.getById(userId);

        PostsResponseDTO response = new PostsResponseDTO();

        List<PostDTO> promoPosts =  postRepository.getAll().stream()
                .filter( post -> post.isHasPromo() && post.getUserId().equals(userId))
                .map( post -> mapper.map(post, PostDTO.class) )
                .collect(Collectors.toList());

        response.setPosts(promoPosts);
        response.setUserId(user.getUserId());

        return response;
    }


}