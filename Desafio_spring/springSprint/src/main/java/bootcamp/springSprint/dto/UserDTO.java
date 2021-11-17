package bootcamp.springSprint.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class User {
    private long userId;
    private String userName;
    private List<User> followers;
    private List<User> followed;


}
