package com.example.socialmeli.unit.service;

import com.example.socialmeli.dto.PostDTO;
import com.example.socialmeli.dto.ProductDTO;
import com.example.socialmeli.dto.UserDTO;
import com.example.socialmeli.dto.response.CountFollowersResponseDTO;
import com.example.socialmeli.dto.response.FollowersResponseDTO;
import com.example.socialmeli.exceptions.NotValidParamException;
import com.example.socialmeli.exceptions.UserAlreadyInUseException;
import com.example.socialmeli.exceptions.UserNotFoundException;
import com.example.socialmeli.exceptions.UserSelfUseException;
import com.example.socialmeli.model.User;
import com.example.socialmeli.repositories.IRepository;
import com.example.socialmeli.repositories.UsuarioRepository;
import com.example.socialmeli.services.SocialMeliService;
import com.google.common.collect.Comparators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        verify(repoUsuarios,atLeastOnce()).findById(any());
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
        service.unfollow(21, userIdToUnfollow);

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(any());
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

    //TODO T-0003 - positivo => DONE
    @Test
    void orderExists() throws UserNotFoundException, NotValidParamException {
        String existingOrder = "name_asc";
        User existingUser = new User();
        //Mock
        when(repoUsuarios.findById(1)).thenReturn(Optional.of(existingUser));
        //Act
        FollowersResponseDTO actualResponse = service.getFollowers(1,existingOrder);

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertTrue(actualResponse.getUserId()!=null);

    }
    //TODO T-0003 - negativo => DONE
    @Test
    @DisplayName("Order no existe")
    void orderDoesNotExist() throws UserNotFoundException,NotValidParamException {
        String nonExistingOrder = "Non Existing Order";
        User existingUser = new User();
        //Mock
        when(repoUsuarios.findById(any())).thenReturn(Optional.of(existingUser));
        //Act

        Throwable exception = assertThrows(NotValidParamException.class, () -> service.getFollowers(1,nonExistingOrder));
        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(NotValidParamException.class, exception.getClass());
    }
    //TODO T-0004

    //TODO T-0005 - positivo
    //TODO T-0005 - negativo

    //TODO T-0006
    @Test
    @DisplayName("Devuelve followers order=name_asc")
    void returnsOrderAscending() throws UserNotFoundException, NotValidParamException {
        //Arrange
        UserDTO user1 = new UserDTO();
        user1.setUserId(60);
        user1.setUserName("Leon Comprador20");
        user1.setFollowersId(new ArrayList<>());

        UserDTO user2 = new UserDTO();
        user2.setUserId(61);
        user2.setUserName("Juan Comprador21");
        user2.setFollowersId(new ArrayList<>());
        //le agrego 2 seguidores
        List<Integer> followers = new ArrayList<>();
        followers.add(user1.getUserId());
        followers.add(user2.getUserId());

        //User new user with followers
        User user = new User();
        user.setUserId(6);
        user.setUserName("Manuel Vendedor");
        user.setFollowersId(followers);

        //Mock
        when(repoUsuarios.findById(any())).thenReturn(Optional.of(user));

        FollowersResponseDTO followersListDto = service.getFollowers(6,"name_asc");
        Comparator<UserDTO> comparator = Comparator.comparing(UserDTO::getUserName);
        assertTrue(Comparators.isInOrder(followersListDto.getFollowers(), comparator));

    }
    @Test
    @DisplayName("Devuelve followers order=name_desc")
    void returnsOrderDescending() throws UserNotFoundException, NotValidParamException {
        //Arrange
        UserDTO user1 = new UserDTO();
        user1.setUserId(602);
        user1.setUserName("Leon Comprador20");
        user1.setFollowersId(new ArrayList<>());

        UserDTO user2 = new UserDTO();
        user2.setUserId(612);
        user2.setUserName("Juan Comprador21");
        user2.setFollowersId(new ArrayList<>());
        //le agrego 2 seguidores
        List<Integer> followers = new ArrayList<>();
        followers.add(user1.getUserId());
        followers.add(user2.getUserId());

        //User new user with followers
        User user = new User();
        user.setUserId(62);
        user.setUserName("Manuel Vendedor");
        user.setFollowersId(followers);

        //Mock
        when(repoUsuarios.findById(any())).thenReturn(Optional.of(user));

        FollowersResponseDTO followersListDto = service.getFollowers(62,"name_desc");
        Comparator<UserDTO> comparator = Comparator.comparing(UserDTO::getUserName);
        assertFalse(Comparators.isInOrder(followersListDto.getFollowers(), comparator));

    }



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
        newUser.setUserId(userIdToCountFollowers);
        newUser.setUserName("Manuel Vendedor7");
        newUser.setFollowersId(followers);
        //Followers List
        List<User> followersList = new ArrayList<>();
        followersList.add(user1);
        followersList.add(user2);

        //Mock
        when(repoUsuarios.findFollowers(any())).thenReturn(followersList);
        //Act
        CountFollowersResponseDTO actualCount = service.countFollowers(userIdToCountFollowers);
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

