package sn.setter.services

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import sn.setter.dto.LoginDto
import sn.setter.dto.UserDto
import sn.setter.generic.SeterException
import sn.setter.models.Role
import sn.setter.models.Utilisateur
import sn.setter.repository.RoleRepository
import sn.setter.repository.UtilisateursRepository
import sn.setter.security.JwtUtil

import java.util.stream.Collectors

@Service
@Slf4j
class UtilisateurService {
    @Autowired
    private UtilisateursRepository utilisateursRepository;

    @Autowired
    RoleRepository roleRepository

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Utilisateur> getAllUsers() {
        try {
            log.info("Liste des utilisateurs retournées avec succes");
            return utilisateursRepository.findAllActiveUsers();
        } catch (SeterException ex) {
            log.error("La liste des utilisateurs est vide: " + ex.toString());
            return Collections.emptyList(); // Renvoie une liste vide en cas d'erreur
        }
        catch (Exception ex) {
            log.error("Une erreur est survenue lors de la récupération des utilisateurs : " + ex.toString());
            throw new SeterException("Une erreur est survenue lors de la récupération des utilisateurs :" + ex.toString())
        }
    }

    def addUser(Utilisateur user) {
        try {
            if (user != null) {
                if (user.prenom== null) {
                    log.error("Le champ prenom est requis");
                    throw new SeterException("Le champ prenom est requis")
                }
                if (user.nom == null){
                    log.error("Le champ nom est requis");
                    throw new SeterException("Le champ nom est requis")
                }
                if (user.telephone == null){
                    log.error("Le champ telephone est requis");
                    throw new SeterException("Le champ telephone est requis")
                }
                if (user.username == null){
                    log.error("Le champ username est requis");
                    throw new SeterException("Le champ username est requis")
                }
                else {
                    log.info("Enregistrement reussie avec succes");
                    return utilisateursRepository.save(user);
                }
            } else {
                log.error("Echec lors de l'ajout d'un nouveau utilisateur");
                throw new SeterException("Le produit à ajouter est null.");
            }
        } catch (Exception ex) {
            log.error("Une erreur est survenue lors de l'ajout d'un utilisateur : {}", ex.toString());
            throw new SeterException("Erreur de validation lors de l'ajout d'un utilisateur"+ ex.toString());
        }
    }

    def getRolesByUsername(String username) {
        Utilisateur utilisateur = utilisateursRepository.findByUsername(username);
        if (utilisateur != null) {
            return utilisateur.getRoles();
        }
        return Collections.emptySet();
    }
    def authenticateUser(@RequestBody LoginDto loginDto) {
        log.info("username :{} ",loginDto.username)
        log.info("password :{} ",loginDto.password)
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Utilisateur user = utilisateursRepository.findByUsername(userDetails.getUsername());

            if (user != null && user.getId() != null) {
                String token = jwtUtil.generateToken(user.getId(),user.getUsername());
                // Construire la réponse au format attendu
                def response =[
                        "access_token": token,
                        "expires_in": 3600
                ]
                return response;
            }
        }

        return null
    }
    def affecterRolesToUser(String userId, List<Role> roleIntitules) {
        try {
            def existingUser = utilisateursRepository.findById(userId)
            def existingRole = null

            if (!existingUser.isPresent()) {
                log.error("L'utilisateur n'existe pas")
                throw new SeterException("L'utilisateur n'existe pas")
            }
            def utilisateur = existingUser.get()
            roleIntitules.each { roleIntitule ->
                existingRole = roleRepository.findByIntitule(roleIntitule.intitule)
                if (!existingRole.isPresent()) {
                    log.error("Le rôle n'existe pas: ${roleIntitule.intitule}")
                    throw new SeterException("Le rôle n'existe pas: ${roleIntitule.intitule}")
                }
                utilisateur.roles << existingRole.get()
            }
            if (utilisateur.getRoles().contains(existingRole.get())) {
                log.error("L'utilisateur ${utilisateur.username} est deja affecte à ce rôle ${existingRole.get().intitule} ")
                throw new SeterException(" L'utilisateur ${utilisateur.username} est deja affecte à ce rôle ${existingRole.get().intitule} ")
            }

            def saveRoleAssign = utilisateursRepository.save(utilisateur)
            if (!saveRoleAssign) {
                log.error("Échec de l'affectation des rôles")
                throw new SeterException("Échec de l'affectation des rôles")
            }
            log.info("affecterRolesToUser : {} ",saveRoleAssign)
            return saveRoleAssign
        } catch (Exception ex) {
            throw new SeterException("Erreur inattendue lors de l'attribution des rôles : " , ex.toString())
        }
    }

    UserDto convertToDto(Utilisateur utilisateur) {
        def existingRole = null
        UserDto utilisateurDto = new UserDto();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setUsername(utilisateur.getUsername())
        utilisateurDto.setRoles(utilisateur.getRoles())

        return utilisateurDto;
    }

}
