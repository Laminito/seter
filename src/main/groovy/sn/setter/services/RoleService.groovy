package sn.setter.services

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sn.setter.generic.SeterException
import sn.setter.models.Role
import sn.setter.repository.RoleRepository

@Service
@Slf4j
class RoleService {
    @Autowired
    RoleRepository roleRepository

    public List<Role> getAllRoles() {
        try {
            log.info("Liste des roles retournées avec succes");
            return roleRepository.findAllActiveRoles();
        } catch (SeterException ex) {
            log.error("La liste des role est vide: " + ex.toString());
            return Collections.emptyList(); // Renvoie une liste vide en cas d'erreur
        }
        catch (Exception ex) {
            log.error("Une erreur est survenue lors de la récupération des roles : " + ex.toString());
            throw new SeterException("Une erreur est survenue lors de la récupération des roles :" + ex.toString())
        }
    }
}
