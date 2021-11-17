package bootcamp.springSprint.dto;

import bootcamp.springSprint.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTOResponse {
    private int userId;
    private String userName;
    private List<User> followers;

    public UserDTOResponse(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.followers = new ArrayList<>();
    }
}
