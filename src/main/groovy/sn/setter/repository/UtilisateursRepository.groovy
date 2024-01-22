package sn.setter.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import sn.setter.models.Utilisateur

@Repository
interface UtilisateursRepository extends CrudRepository<Utilisateur,String>{
    @Query("SELECT user FROM Utilisateur user WHERE user.enabled = true")
    List<Utilisateur> findAllActiveUsers();
    Utilisateur findByUsernameOrEmail(String username, String email);
    Utilisateur findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Utilisateur u SET u.roles = NULL WHERE u.id = :userId AND :roleId IN (SELECT r.id FROM u.roles r)")
    void removeRoleFromUser(@Param("userId") String userId, @Param("roleId") String roleId);


}