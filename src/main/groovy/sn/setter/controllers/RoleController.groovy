package sn.setter.controllers

import groovy.util.logging.Slf4j
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import sn.setter.dto.AssignRolesRequestDto
import sn.setter.dto.RetirerRoleDto
import sn.setter.dto.RoleDto
import sn.setter.dto.Seter
import sn.setter.dto.UserDto
import sn.setter.generic.CustomResponse
import sn.setter.generic.ResponseFactory
import sn.setter.generic.SeterException
import sn.setter.models.Role
import sn.setter.models.Utilisateur
import sn.setter.repository.RoleRepository
import sn.setter.repository.UtilisateursRepository
import sn.setter.services.RoleService
import sn.setter.services.UtilisateurService
import sn.setter.utils.Constantes

import java.util.stream.Collectors

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/roles")
@Slf4j
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UtilisateursRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    Map<String, CustomResponse> resultMap;
    Seter responseDto = new Seter();
    CustomResponse errorResponse = null;
    CustomResponse successResponse = null

    // Endpoint pour récupérer tous les rôles
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list")
    public Seter getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            if (roles.isEmpty() || roles == null){
                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La liste des roles est vide ",
                        roles
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                log.info("roles : {} ",roles)
                responseDto.setSeter(resultMap);
                log.error("La liste des roles et vide");
                return responseDto;
            }

            List<RoleDto> roleDtos = roles.stream()
                    .map(role -> {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setId(role.getId())
                        roleDto.setIntitule(role.getIntitule())
                        roleDto.setCreatedDate(role.getCreatedDate());
                        return roleDto;
                    })
                    .collect(Collectors.toList());

            successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "Récupération de la liste des roles a réussie avec succès",
                    roleDtos
            );

            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            log.info("Récupération de la liste des roles a réussie avec succès");
            responseDto.setSeter(resultMap);
        } catch (DataAccessException ex) {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur de base de données lors de la récupération des roles ",
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Erreur de base de données lors de la récupération des roles ");
        }
        catch (Exception ex) {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de la récupération de la liste des roles ",
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de la récupération de la liste des roles : " + ex.toString());
        }
        return responseDto
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public Seter createRole(@RequestBody Role role) {
        try {
        if(role == null || role.intitule == null){
            log.error("Le champ intitule est requis");
            throw new SeterException("Le champ intitule est requis");
        }
        if(!roleRepository.save(role)){
            log.error("L'enregistrement d'un nouveau role a echoué");
            throw new SeterException("L'enregistrement d'un nouveau role a echoué");
        }
        Role addedNewRole = roleRepository.save(role)
        RoleDto roleDto = new RoleDto()
        roleDto.setId(addedNewRole.getId())
        roleDto.setIntitule(addedNewRole.getIntitule())
        roleDto.setCreatedDate(addedNewRole.getCreatedDate())

        successResponse = ResponseFactory.seterResponse(
                Constantes.Message.SUCCESS_BODY,
                Constantes.Status.OK,
                "Enregistrement réussi avec succès",
                roleDto
        );

        resultMap = new HashMap<>();
        resultMap.put("result", successResponse);
        responseDto.setSeter(resultMap);

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
            log.error("Une erreur s'est produite lors de l'ajout d'un nouveau role: " + ex.toString());

        } catch (Exception ex) {
            log.error("Une erreur est survenue lors de l'ajout d'un role : {}", ex.toString());
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de l'ajout d'un role",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de l'ajout d'un role " + ex.toString());
        }
        return responseDto;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/assign")
    public Seter assignRolesToUser(@RequestBody AssignRolesRequestDto request) {
        try {
            if(request != null){
                if (request.getRoleIntitules() == null) {
                    log.error("Le champ intitulé est requis");
                    throw new SeterException("Le champ intitulé est requis");
                }
                if (request.getUserId() == null) {
                    log.error("Le champ userId est requis");
                    throw new SeterException("Le champ userId est requis");
                }
                else{
                    Utilisateur addedRole = utilisateurService.affecterRolesToUser(request.getUserId(), request.getRoleIntitules());
                    if(!addedRole){
                        log.error("L'affectation d'un nouveau role à un user a echoué");
                        throw new SeterException("L'affectation d'un nouveau role à un user a echoué");
                    }
                    // Filtrer les rôles et récupérer uniquement l'id et l'intitulé
                    List<RoleDto> filteredRoles = addedRole.getRoles().stream()
                            .map(role -> {
                                RoleDto roleDto = new RoleDto();
                                roleDto.setId(role.getId());
                                roleDto.setIntitule(role.getIntitule());
                                return roleDto;
                            })
                            .collect(Collectors.toList());

                    UserDto userDto = new UserDto()
                    userDto.setId(addedRole.getId())
                    userDto.setPrenom(addedRole.getUsername())
                    userDto.setNom(addedRole.getNom())
                    userDto.setUsername(addedRole.getUsername())
                    userDto.setRoles(filteredRoles)

                    successResponse = ResponseFactory.seterResponse(
                            Constantes.Message.SUCCESS_BODY,
                            Constantes.Status.OK,
                            "L'affectation de role a réussi avec succès",
                            userDto
                    );

                    resultMap = new HashMap<>();
                    resultMap.put("result", successResponse);
                    responseDto.setSeter(resultMap);
                    log.info("{assignRolesToUser} : ",responseDto)
                    return responseDto;
                }

            }

        } catch (Exception ex) {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur lors de l'attribution des rôles",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Erreur lors de l'attribution des rôles" ,ex.toString());
            return responseDto

        }

    }

/*    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/remove")
    public Seter retirerRoleAUtilisateur(@RequestBody RetirerRoleDto retirerRoleDto) {
        log.info("userId : {} ",retirerRoleDto.userId)
        log.info("roleId : {} ",retirerRoleDto.roleId)
        try {
            def existingUser = utilisateurRepository.findById(retirerRoleDto.userId)
            def existingRole = roleRepository.findById(retirerRoleDto.roleId)

            if (!existingUser.isPresent()) {
                log.error("L'utilisateur n'existe pas")
                throw new SeterException("L'utilisateur n'existe pas");
            }
            if (!existingRole.isPresent()) {
                log.error("Le role n'existe pas")
                throw new SeterException("Le role n'existe pas");
            }
            else {
                def savedUser = utilisateurRepository.removeRoleFromUser(retirerRoleDto.userId,retirerRoleDto.userId)
              *//*  def utilisateur = existingUser.get()
                utilisateur.getRoles().remove(existingRole.get())

                // Enregistrez les modifications dans la base de données
                def savedUser = utilisateurRepository.save(utilisateur)
                // Filtrer les rôles et récupérer uniquement l'id et l'intitulé
                List<RoleDto> filteredRoles = savedUser.getRoles().stream()
                        .map(role -> {
                            RoleDto roleDto = new RoleDto();
                            roleDto.setId(role.getId());
                            roleDto.setIntitule(role.getIntitule());
                            return roleDto;
                        })
                        .collect(Collectors.toList());
                def utilisateurDto = utilisateurService.convertToDto(savedUser);
                UserDto  user = new  UserDto()
                user.setId(utilisateurDto.getId())
                user.setUsername(utilisateurDto.getUsername())
                user.setRoles(filteredRoles)*//*
                successResponse = ResponseFactory.seterResponse(
                        Constantes.Message.SUCCESS_BODY,
                        Constantes.Status.OK,
                        "La suppression du role ${existingRole.get().intitule} a réussi avec succès",
                        savedUser
                );

                resultMap = new HashMap<>();
                resultMap.put("result", successResponse);
                responseDto.setSeter(resultMap);
                log.info("{assignRolesToUser} : ",responseDto)
                return responseDto;
            }
        }catch(SeterException ex){
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur lors de la suppression de role",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Erreur lors de suppression de role" ,ex.toString());
            return responseDto

        }
    }*/

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/remove")
    public Seter retirerRoleAUtilisateur(@RequestBody RetirerRoleDto retirerRoleDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(retirerRoleDto.userId).orElse(null);
        Role role = roleRepository.findById(retirerRoleDto.roleId).orElse(null);

        if (utilisateur != null && role != null) {
            utilisateur.getRoles().remove(role);
            log.info("utilisateur.getRoles().remove(role) : {} ",utilisateur.getRoles().remove(role.intitule).toString())
            def result = utilisateurRepository.save(utilisateur);
            UserDto userDto = new UserDto()
            userDto.setId(result.getId())
            userDto.setPrenom(result.getUsername())
            userDto.setNom(result.getNom())
            userDto.setUsername(result.getUsername())

            successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "La suppression de role a réussi avec succès",
                    userDto
            );

            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            responseDto.setSeter(resultMap);
            return responseDto;
        } else {
            // TODO Gérer le cas où l'utilisateur ou le rôle n'existe pas
            throw new EntityNotFoundException("L'utilisateur ou le rôle n'existe pas");
        }
    }

}
