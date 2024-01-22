package sn.setter.dto

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Getter
import lombok.Setter
import sn.setter.models.Role

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserDto{
    String id;
    String prenom;
    String nom;
    String telephone;
    String username;
    String password;
    String email;
    Object createdDate;
    Object lastModifiedDate;
    List<Role> roles;
}