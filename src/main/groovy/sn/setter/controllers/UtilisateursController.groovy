package sn.setter.controllers

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sn.setter.dto.Seter
import sn.setter.dto.UserDto
import sn.setter.generic.CustomResponse
import sn.setter.generic.ResponseFactory
import sn.setter.generic.SeterException
import sn.setter.models.Utilisateur
import sn.setter.services.UtilisateurService
import sn.setter.utils.Constantes

import java.util.stream.Collectors

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
class UtilisateursController {
    Map<String, CustomResponse> resultMap;
    Seter responseDto = new Seter();
    CustomResponse errorResponse = null;
    CustomResponse successResponse = null

    @Autowired
    UtilisateurService utilisateurService

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Seter getAllUsers(){
        try {
            List<Utilisateur> users = utilisateurService.getAllUsers();
            if (users.isEmpty() || users == null){

                 errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La liste des utilisateurs est vide ",
                        users
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                log.info("users : {} ",users)
                responseDto.setSeter(resultMap);
                log.error("La liste des utilisateurs et vide");
                return responseDto;
            }

            List<UserDto> userDtos = users.stream()
                    .map(utilisateur -> {
                        UserDto userDto = new UserDto();
                        userDto.setId(utilisateur.getId());
                        userDto.setPrenom(utilisateur.getPrenom());
                        userDto.setNom(utilisateur.getNom());
                        userDto.setUsername(utilisateur.getUsername());
                        userDto.setRoles(utilisateur.getRoles().intitule)
                        userDto.setEmail(utilisateur.getEmail());
                        userDto.setCreatedDate(utilisateur.getCreatedDate());
                        return userDto;
                    })
                    .collect(Collectors.toList());

             successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "Récupération de la liste des utilisateurs a réussie avec succès",
                    userDtos
            );

            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            log.info("Récupération de la liste des utilisateurs a réussie avec succès");
            responseDto.setSeter(resultMap);
        }
        catch (DataAccessException ex) {
             errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur de base de données lors de la récupération des produits ",
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Erreur de base de données lors de la récupération des produits ");
        }
        catch (Exception ex) {
             errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de la récupération de la liste des utilisateurs ",
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de la récupération de la liste des produits : " + ex.toString());
        }
        return responseDto;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
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
                    Utilisateur addedUser = utilisateurService.addUser(user);
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
