package com.example.socialmeli.unit.service;

import com.example.socialmeli.dto.UserDTO;
import com.example.socialmeli.dto.response.CountFollowersResponseDTO;
import com.example.socialmeli.dto.response.FollowersResponseDTO;
import com.example.socialmeli.dto.response.PostsResponseDTO;
import com.example.socialmeli.exceptions.NotValidParamException;
import com.example.socialmeli.exceptions.UserAlreadyInUseException;
import com.example.socialmeli.exceptions.UserNotFoundException;
import com.example.socialmeli.exceptions.UserSelfUseException;
import com.example.socialmeli.model.User;
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
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.meta.When;
import java.util.ArrayList;
import java.util.Comparator;
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


    @AfterEach
    void resetData(){
        reset(repoUsuarios);
    }
    // T-0001 - positivo => DONE

    @Test
    @DisplayName("Seguir usuario existente")
    void followExistingUser() throws UserNotFoundException, UserAlreadyInUseException, UserSelfUseException {

        //Arrange
        User follower = new User();
        follower.setUserId(10);
        follower.setUserName("Leon Comprador10");
        follower.setFollowersId(new ArrayList<>());

        User followed = new User();
        followed.setUserId(11);
        followed.setUserName("Juan Comprador11");
        followed.setFollowersId(new ArrayList<>());

        //Mocks
        when(repoUsuarios.findById(10)).thenReturn(Optional.of(follower));
        when(repoUsuarios.findById(11)).thenReturn(Optional.of(followed));

        //Act
        service.follow(follower.getUserId(), followed.getUserId());

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(10);
        verify(repoUsuarios,atLeastOnce()).findById(11);
        assertEquals(1, followed.getFollowersId().size());

    }

    // T-0001 - negativo => DONE
    @Test
    @DisplayName("Seguir usuario NO existente")
    void followNonExistingUser(){
        int userIdDoesNotExists = 809;
        when(repoUsuarios.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(UserNotFoundException.class, () -> service.follow(1,userIdDoesNotExists));

        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(UserNotFoundException.class, exception.getClass());
    }

    // T-0002 - positivo => DONE
    @Test
    @DisplayName("Dejar de seguir usuario existente")
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
        //Act => user id 21 deja de seguir a 5
        service.unfollow(21, userIdToUnfollow);

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(userIdToUnfollow);
        assertEquals(1, userToUnfollow.getFollowersId().size()); //queda un follower(id 20)
        assertTrue(userToUnfollow.getFollowersId().contains(20)); //validar que contenga el id 20 como follower
    }

    // T-0002 - negativo => DONE
    @Test
    @DisplayName("Dejar de seguir usuario NO existente")
    void UserToUnfollowNotFound(){
        int userIdDoesNotExists = 808;
        int idExistingUser = 1;
        when(repoUsuarios.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(UserNotFoundException.class, () -> service.unfollow(idExistingUser,userIdDoesNotExists));
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(UserNotFoundException.class, exception.getClass());
    }

    // T-0003 - positivo => DONE
    @Test
    @DisplayName("Orden por nombre existe")
    void orderByNameExists() throws UserNotFoundException, NotValidParamException {
        String existingOrder = "name_asc";
        User existingUser = new User();
        //Mock
        when(repoUsuarios.findById(1)).thenReturn(Optional.of(existingUser));
        //Act
        FollowersResponseDTO actualResponse = service.getFollowers(1,existingOrder);

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertNotNull(actualResponse.getUserId());

    }
    // T-0003 - negativo => DONE
    @Test
    @DisplayName("Ordenamiento de nombre no existe")
    void orderByNameDoesNotExist() throws UserNotFoundException,NotValidParamException {
        String nonExistingOrder = "Non Existing Order By Name";
        //Mock
        when(repoUsuarios.findById(any())).thenReturn(Optional.of(new User()));
        //Act

        Throwable exception = assertThrows(NotValidParamException.class, () -> service.getFollowers(1,nonExistingOrder));
        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(any());
        assertEquals(NotValidParamException.class, exception.getClass());
    }
    // T-0004 => DONE
    @Test
    @DisplayName("Devuelve followers order=name_asc")
    void returnsOrderByNameAscending() throws UserNotFoundException, NotValidParamException {
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
        assertEquals(2,followersListDto.getFollowers().size());

    }
    @Test
    @DisplayName("Devuelve followers order=name_desc")
    void returnsOrderByNameDescending() throws UserNotFoundException, NotValidParamException {
        //Arrange
        User user1 = new User();
        user1.setUserId(602);
        user1.setUserName("Barbara");
        user1.setFollowersId(new ArrayList<>());

        User user2 = new User();
        user2.setUserId(612);
        user2.setUserName("Juan");
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
        List<Integer> listOfIDsExpected = List.of(612,602);
        //Mock
        when(repoUsuarios.findById(602)).thenReturn(Optional.of(user1));
        when(repoUsuarios.findById(612)).thenReturn(Optional.of(user2));
        when(repoUsuarios.findById(62)).thenReturn(Optional.of(user));

        FollowersResponseDTO followersListDto = service.getFollowers(62,"name_desc");
        //En orden desc primero debe estar Juan(id 612) y luego Barbara(id 602)
        assertEquals(listOfIDsExpected.get(0),followersListDto.getFollowers().get(0).getUserId());
        assertEquals("Juan",followersListDto.getFollowers().get(0).getUserName());

        assertEquals(listOfIDsExpected.get(1),followersListDto.getFollowers().get(1).getUserId());
        assertEquals("Barbara",followersListDto.getFollowers().get(1).getUserName());

        assertEquals(2,followersListDto.getFollowers().size());

    }

    // T-0005 - positivo => DONE
    @Test
    @DisplayName("Orden por fecha existe")
    void orderByDateExistS() throws UserNotFoundException, NotValidParamException {
        String existingOrder = "date_asc";
        User existingUser = new User();
        //Mock
        when(repoUsuarios.findById(1)).thenReturn(Optional.of(existingUser));
        //Act
        PostsResponseDTO actualResponse = service.getFollowedPostList(1,existingOrder);

        //Assert
        verify(repoUsuarios,atLeastOnce()).findById(1);
        assertNotNull(actualResponse.getUserId());
    }

    // T-0005 - negativo => DONE
    @Test
    @DisplayName("Orden por fecha no existe")
    void orderByDateDoesNotExist() throws UserNotFoundException,NotValidParamException {
        String nonExistingOrder = "Non Existing Order Date";

        //Act
        Throwable exception = assertThrows(NotValidParamException.class, () -> service.getFollowedPostList(1,nonExistingOrder));
        //Assert

        assertEquals(NotValidParamException.class, exception.getClass());
    }

    //TODO T-0006 => PENDING

    // T-0007 => DONE
    @Test
    @DisplayName("Cantidad de followers")
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
        when(repoUsuarios.findFollowers(userIdToCountFollowers)).thenReturn(followersList);
        when(repoUsuarios.findById(userIdToCountFollowers)).thenReturn(Optional.of(newUser));
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

