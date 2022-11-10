package dtos;

import entities.User;

import java.util.List;

public class UserDTO {

    String name;
    List<String> roles;

    public UserDTO(User user) {
        this.name = user.getUserName();
        this.roles = user.getRolesAsStrings();
    }
}
