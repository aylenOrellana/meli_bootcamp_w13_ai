package com.bootcamp.SocialMeli.repository;


import java.util.List;

public interface UserRepository {

    List<UserDTO> getUsers();
    boolean userExists(int id);

}
