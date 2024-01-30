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
import sn.setter.dto.GareDto
import sn.setter.generic.CustomResponse
import sn.setter.generic.ResponseFactory
import sn.setter.generic.SeterException
import sn.setter.models.Gare
import sn.setter.services.GareService
import sn.setter.utils.Constantes

import java.util.stream.Collectors

@RestController
@RequestMapping("/gare")
@Slf4j
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
class GareController
{
    Map<String, CustomResponse> resultMap;
    Seter responseDto = new Seter();
    CustomResponse errorResponse = null;
    CustomResponse successResponse = null

    @Autowired
    GareService gareService

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Seter getAllGares(){
        try {
            List<Gare> gares = gareService.getAllGares();
            if (gares.isEmpty() || gares == null){

                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La liste des gares est vide ",
                        gares
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                log.info("gares : {} ",gares)
                responseDto.setSeter(resultMap);
                log.error("La liste des gares est vide");
                return responseDto;
            }

            List<GareDto> gareDtos = gares.stream()
                    .map(gare -> {
                        GareDto gareDto = new GareDto();
                        gareDto.setId(gare.getId());
                        gareDto.setNom(gare.getNom());
                        gareDto.setAdresse(gare.getAdresse());
                        gareDto.setLatitude(gare.getLatitude())
                        gareDto.setLongitude(gare.getLongitude());
                        gareDto.setEnabled(gare.getEnabled());
                        return gareDto;
                    })
                    .collect(Collectors.toList());

            successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "La récupération de la liste des gares a été un succès",
                    gareDtos
            );

            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            log.info("La récupération de la liste des gares a été un succès");
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
                    "Une erreur s'est produite lors de la récupération de la liste des gares ",
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
    @PostMapping("/add")
    public Seter addGare(@RequestBody Gare gare) {
        try {
            if (gare != null) {
                if (gare.getNom() == null) {
                    log.error("Le champ nom est requis");
                    throw new SeterException("Le champ nom est requis");
                }
                if (gare.getAdresse() == null) {
                    log.error("Le champ adresse est requis");
                    throw new SeterException("Le champ adresse est requis");
                }
                if (gare.getLatitude() == 0) {
                    log.error("Le champ latitude est requis");
                    throw new SeterException("Le champ latitude est requis");
                }
                if (gare.getLongitude() == 0) {
                    log.error("Le champ longitude est requis");
                    throw new SeterException("Le champ longitude est requis");
                }
                else {
                    Gare addedGare = gareService.addGare(gare);
                    if(!addedGare){
                        log.error("L'enregistrement d'une nouvelle gare a échoué");
                        throw new SeterException("L'enregistrement d'une nouvelle gare a échoué");
                    }
                    GareDto gareDto = new GareDto();
                    gareDto.setId(addedGare.getId());
                    gareDto.setNom(addedGare.getNom());
                    gareDto.setAdresse(addedGare.getAdresse());
                    gareDto.setLatitude(addedGare.getLatitude());
                    gareDto.setLongitude(addedGare.getLongitude());
                    gareDto.setEnabled(addedGare.getEnabled());
                    gareDto.setCreatedDate(addedGare.getCreatedDate());
                    successResponse = ResponseFactory.seterResponse(
                            Constantes.Message.SUCCESS_BODY,
                            Constantes.Status.OK,
                            "Enregistrement réussi avec succès",
                            gareDto
                    );

                    resultMap = new HashMap<>();
                    resultMap.put("result", successResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
            } else {
                log.error("Échec lors de l'ajout d'une nouvelle gare");
                throw new SeterException("La gare à ajouter est nulle.");
            }
        } catch (SeterException setex) {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de l'ajout d'une nouvelle gare: " + setex.toString());

        } catch (Exception ex) {
            log.error("Une erreur est survenue lors de l'ajout d'une nouvelle gare : {}", ex.toString());
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de l'ajout d'une nouvelle gare",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
            log.error("Une erreur s'est produite lors de l'ajout d'une nouvelle gare " + ex.toString());
        }
    }
}
