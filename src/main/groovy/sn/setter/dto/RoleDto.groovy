package sn.setter.dto

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class RoleDto {
    String id
    String intitule
    Object createdDate;
    Object lastModifiedDate;
}
