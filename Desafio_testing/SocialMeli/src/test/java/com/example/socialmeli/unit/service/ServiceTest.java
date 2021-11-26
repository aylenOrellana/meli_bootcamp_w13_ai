package com.example.socialmeli.unit.service;

import com.example.socialmeli.dto.PostDTO;
import com.example.socialmeli.dto.ProductDTO;
import com.example.socialmeli.dto.response.CountFollowersResponseDTO;
import com.example.socialmeli.exceptions.UserAlreadyInUseException;
import com.example.socialmeli.exceptions.UserNotFoundException;
import com.example.socialmeli.exceptions.UserSelfUseException;
import com.example.socialmeli.model.User;
import com.example.socialmeli.repositories.IRepository;
import com.example.socialmeli.repositories.UsuarioRepository;
import com.example.socialmeli.services.SocialMeliService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @Mock
    UsuarioRepository repoUsuarios;
    @InjectMocks
    SocialMeliService service;


/*    @AfterEach
    void resetData(){
        reset(repoUsuarios);
    }*/
    //TODO T-0001 - positivo => DONE

    @Test

    void followExistingUser() throws UserNotFoundException, UserAlreadyInUseException, UserSelfUseException {

        //Arrange
        int userIdTofollow = 11;
        User follower = new User();
        follower.setUserId(10);
        follower.setUserName("Leon Comprador10");
        follower.setFollowersId(new ArrayList<>());

        User followed = new User();
        followed.setUserId(11);
        followed.setUserName("Juan Comprador11");
        followed.setFollowersId(new ArrayList<>());

        //Mocks
        when(repoUsuarios.findById(any())).thenReturn(Optional.of(followed));
        //Act
        service.follow(follower.getUserId(), followed.getUserId());
        //Assert
        assertEquals(1, followed.getFollowersId().size());

    }



    //TODO T-0001 - negativo => DONE
    @Test
    void followNonExistingUser(){
        int userIdDoesNotExists = 809;
        when(repoUsuarios.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(UserNotFoundException.class, () -> service.follow(1,userIdDoesNotExists));

        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(UserNotFoundException.class, exception.getClass());
    }

    //TODO T-0002 - positivo => DONE
    @Test
    void unfollowUser() throws UserNotFoundException, UserAlreadyInUseException, UserSelfUseException {
        //Arrange
        int userIdToUnfollow = 6;
        User user1 = new User();
        user1.setUserId(20);
        user1.setUserName("Leon Comprador20");
        user1.setFollowersId(new ArrayList<>());

        User user2 = new User();
        user2.setUserId(21);
        user2.setUserName("Juan Comprador21");
        user2.setFollowersId(new ArrayList<>());
        //le agrego 2 seguidores
        List<Integer> followers = new ArrayList<>();
        followers.add(user1.getUserId());
        followers.add(user2.getUserId());

        //User userToUnfollow
        User userToUnfollow = new User();
        userToUnfollow.setUserId(5);
        userToUnfollow.setUserName("Manuel Vendedor");
        userToUnfollow.setFollowersId(followers);

        //Mocks
        when(repoUsuarios.findById(userIdToUnfollow)).thenReturn(Optional.of(userToUnfollow));
        //Act
        service.unfollow(11, userIdToUnfollow);
        Integer expected = 12;
        //Assert
        assertEquals(1, userToUnfollow.getFollowersId().size());
        //assertTrue(userToUnfollow.getFollowersId().contains(expected));
    }

    //TODO T-0002 - negativo => DONE
    @Test
    void UserToUnfollowNotFound(){
        int userIdDoesNotExists = 808;
        int idExistingUser = 1;
        when(repoUsuarios.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(UserNotFoundException.class, () -> service.unfollow(idExistingUser,userIdDoesNotExists));
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(UserNotFoundException.class, exception.getClass());
    }

    //TODO T-0003 - positivo
    //
    //TODO T-0003 - negativo
    //TODO T-0004 - positivo
    //TODO T-0004 - negativo
    //TODO T-0005 - positivo
    //TODO T-0005 - negativo
    //TODO T-0006

    //TODO T-0007 => HECHO PERO FUNCIONANDO MAL :(
    @Test
    void countFollowers() throws UserNotFoundException {
        //Arrange
        int userIdToCountFollowers= 7;
        User user1 = new User();
        user1.setUserId(20);
        user1.setUserName("Leon Comprador20");
        user1.setFollowersId(new ArrayList<>());

        User user2 = new User();
        user2.setUserId(21);
        user2.setUserName("Juan Comprador21");
        user2.setFollowersId(new ArrayList<>());
        //le agrego 2 seguidores
        List<Integer> followers = new ArrayList<>();
        followers.add(user1.getUserId());
        followers.add(user2.getUserId());
        //new Userid 7 with 2 followers
        User newUser = new User();
        newUser.setUserId(7);
        newUser.setUserName("Manuel Vendedor7");
        newUser.setFollowersId(followers);
        //Followers List
        List<User> followersList = new ArrayList<>();
        followersList.add(user1);
        followersList.add(user2);

        //Mock
        when(repoUsuarios.findFollowers(any())).thenReturn(followersList);
        //Act
        CountFollowersResponseDTO actualCount = service.countFollowers(7);
        //Assert
        assertEquals(2,actualCount.getFollowersCount());


    }
    //TODO T-0008

/*    void postLast2Weeks(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
        String date = "18-12-2021";
        ProductDTO newProduct1 = new ProductDTO(1,"Silla Gamer","Tipo1","Racer","RedBlack","Special Edition");
        PostDTO newPost1 = new PostDTO(1,1,"18-12-2021",newProduct1,100,1000,false,0.0);
    }*/


}

