package sn.setter.controllers

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sn.setter.dto.LoginDto
import sn.setter.dto.Seter
import sn.setter.dto.UserDto
import sn.setter.generic.CustomResponse
import sn.setter.generic.ResponseFactory
import sn.setter.generic.SeterException
import sn.setter.models.Utilisateur
import sn.setter.repository.RoleRepository
import sn.setter.repository.UtilisateursRepository
import sn.setter.services.UtilisateurService
import sn.setter.utils.Constantes
import sn.setter.utils.Tools

@RestController
@RequestMapping("/auth")
@Slf4j
class SecurityController {
    Map<String, CustomResponse> resultMap;
    Seter responseDto = new Seter();
    CustomResponse errorResponse = null;
    CustomResponse successResponse = null
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UtilisateursRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UtilisateurService userService;

   @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            def token = userService.authenticateUser(loginDto);
            if (token != null) {
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User login failed!...");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User login failed!...");
    }

    @PostMapping("/signup")
    public Seter addUser(@RequestBody Utilisateur user) {
        try {
            if (user != null) {
                if (user.getPrenom() == null) {
                    log.error("Le champ prénom est requis");
                    throw new SeterException("Le champ prénom est requis");
                }
                if (user.getNom() == null) {
                    log.error("Le champ nom est requis");
                    throw new SeterException("Le champ nom est requis");
                }
                if (user.getTelephone() == null) {
                    log.error("Le champ téléphone est requis");
                    throw new SeterException("Le champ téléphone est requis");
                }
                if (user.getUsername() == null) {
                    log.error("Le champ username est requis");
                    throw new SeterException("Le champ username est requis");
                } else {
                    Utilisateur addedUser = userService.addUser(user);
                    if(!addedUser){
                        log.error("L'enregistrement d'un nouveau user a echoué");
                        throw new SeterException("L'enregistrement d'un nouveau user a echoué");
                    }
                    UserDto userDto = new UserDto();
                    userDto.setId(addedUser.getId());
                    userDto.setPrenom(addedUser.getPrenom());
                    userDto.setNom(addedUser.getNom());
                    userDto.setUsername(addedUser.getUsername());
                    userDto.setEmail(addedUser.getEmail());
                    userDto.setCreatedDate(addedUser.getCreatedDate());
                    successResponse = ResponseFactory.seterResponse(
                            Constantes.Message.SUCCESS_BODY,
                            Constantes.Status.OK,
                            "Enregistrement réussi avec succès",
                            userDto
                    );

                    resultMap = new HashMap<>();
                    resultMap.put("result", successResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
            } else {
                log.error("Échec lors de l'ajout d'un nouvel utilisateur");
                throw new SeterException("L'utilisateur à ajouter est null.");
            }
        } catch (SeterException ex) {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de l'ajout d'un nouveau user: " + ex.toString());

        } catch (Exception ex) {
            log.error("Une erreur est survenue lors de l'ajout d'un utilisateur : {}", ex.toString());
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de l'ajout d'un utilisateur",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de l'ajout d'un utilisateur " + ex.toString());

        }
    }
}
