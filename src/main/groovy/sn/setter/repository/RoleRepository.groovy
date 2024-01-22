package sn.setter.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sn.setter.models.Role

@Repository
interface RoleRepository extends CrudRepository<Role, String> {
    Optional<Role> findByIntitule(String intitule);
    @Query("SELECT role FROM Role role WHERE role.enabled = true")
    List<Role> findAllActiveRoles();
}