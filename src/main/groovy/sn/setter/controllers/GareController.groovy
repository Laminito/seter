package sn.setter.controllers

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
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
    public Seter getAllGares()
    {
        try
        {
            List<Gare> gares = gareService.getAllGares();
            if (gares.isEmpty() || gares == null)
            {
                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La liste des gares est vide ",
                        gares
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                responseDto.setSeter(resultMap);
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
            responseDto.setSeter(resultMap);
        }
        catch (DataAccessException ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur de base de données lors de la récupération des gares " + ex.toString(),
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        catch (Exception ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de la récupération de la liste des gares : " + ex.toString(),
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        return responseDto;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public Seter addGare(@RequestBody Gare gare)
    {
        try
        {
            if (gare != null)
            {
                if (gare.getNom() == null)
                {
                    errorResponse = ResponseFactory.seterResponse(
                            Constantes.Message.BAD_REQUEST_BODY,
                            Constantes.Status.BAD_REQUEST,
                            "Le champ nom est requis",
                            null
                    );
                    resultMap = new HashMap<>();
                    resultMap.put("result", errorResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
                if (gare.getAdresse() == null)
                {
                    errorResponse = ResponseFactory.seterResponse(
                            Constantes.Message.BAD_REQUEST_BODY,
                            Constantes.Status.BAD_REQUEST,
                            "Le champ adresse est requis",
                            null
                    );
                    resultMap = new HashMap<>();
                    resultMap.put("result", errorResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
                if (gare.getLatitude() == 0)
                {
                    errorResponse = ResponseFactory.seterResponse(
                            Constantes.Message.BAD_REQUEST_BODY,
                            Constantes.Status.BAD_REQUEST,
                            "Le champ latitude est requis",
                            null
                    );
                    resultMap = new HashMap<>();
                    resultMap.put("result", errorResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
                if (gare.getLongitude() == 0)
                {
                    errorResponse = ResponseFactory.seterResponse(
                            Constantes.Message.BAD_REQUEST_BODY,
                            Constantes.Status.BAD_REQUEST,
                            "Le champ longitude est requis",
                            null
                    );
                    resultMap = new HashMap<>();
                    resultMap.put("result", errorResponse);
                    responseDto.setSeter(resultMap);
                    return responseDto;
                }
                else
                {
                    Gare addedGare = gareService.addGare(gare);
                    if(!addedGare)
                    {
                        errorResponse = ResponseFactory.seterResponse(
                                Constantes.Message.BAD_REQUEST_BODY,
                                Constantes.Status.BAD_REQUEST,
                                "L'enregistrement d'une nouvelle gare a échoué.",
                                null
                        );
                        resultMap = new HashMap<>();
                        resultMap.put("result", errorResponse);
                        responseDto.setSeter(resultMap);
                        return responseDto;
                    }
                    GareDto gareDto = new GareDto();
                    gareDto.setId(addedGare.getId());
                    gareDto.setNom(addedGare.getNom());
                    gareDto.setAdresse(addedGare.getAdresse());
                    gareDto.setLatitude(addedGare.getLatitude());
                    gareDto.setLongitude(addedGare.getLongitude());
                    gareDto.setEnabled(addedGare.getEnabled());
                    gareDto.setCreatedDate(addedGare.getCreatedDate());
                    gareDto.setLastModifiedDate(addedGare.getLastModifiedDate());
                    successResponse = ResponseFactory.seterResponse(
                            Constantes.Message.SUCCESS_BODY,
                            Constantes.Status.OK,
                            "Enregistrement réussi avec succès",
                            gareDto
                    );
                    resultMap = new HashMap<>();
                    resultMap.put("result", successResponse);
                    responseDto.setSeter(resultMap);
                }
            }
            else
            {
                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.BAD_REQUEST_BODY,
                        Constantes.Status.BAD_REQUEST,
                        "Échec lors de l'ajout d'une nouvelle gare : La gare à ajouter est nulle.",
                        null
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                responseDto.setSeter(resultMap);
                return responseDto;
            }
        }
        catch (SeterException ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de l'ajout d'une nouvelle gare: " + ex.toString(),
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        catch (Exception ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de l'ajout d'une nouvelle gare:  " + ex.toString(),
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        return responseDto;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{gareId}")
    public Seter getGare(@PathVariable("gareId") String gareId)
    {
        try
        {
            Gare gare = gareService.getGareById(gareId);
            if (gare == null)
            {
                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La gare avec l'ID " + gareId + " n'a pas été trouvée",
                        null
                );

                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                log.error("La gare avec l'ID " + gareId + " n'a pas été trouvée");
                return responseDto;
            }
            GareDto gareDto = new GareDto();
            gareDto.setId(gare.getId());
            gareDto.setNom(gare.getNom());
            gareDto.setAdresse(gare.getAdresse());
            gareDto.setLatitude(gare.getLatitude());
            gareDto.setLongitude(gare.getLongitude());
            gareDto.setEnabled(gare.getEnabled());
            successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "La récupération de la gare avec l'ID " + gareId + " a été un succès",
                    Collections.singletonList(gareDto)
            );
            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            responseDto.setSeter(resultMap);
        }
        catch (DataAccessException ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur de base de données lors de la récupération de la gare avec l'ID " + gareId + " : " + ex.toString(),
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        catch (Exception ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de la récupération de la gare avec l'ID " + gareId + " : " + ex.toString(),
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        return responseDto;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{gareId}")
    public Seter deleteGare(@PathVariable("gareId") String gareId)
    {
        try
        {
            Gare gare = gareService.getGareById(gareId);
            if (gare == null)
            {
                errorResponse = ResponseFactory.seterResponse(
                        Constantes.Message.NOT_FOUND_BODY,
                        Constantes.Status.NOT_FOUND,
                        "La gare avec l'ID " + gareId + " n'a pas été trouvée",
                        null
                );
                resultMap = new HashMap<>();
                resultMap.put("result", errorResponse);
                return responseDto;
            }
            gare.setEnabled(false);
            gareService.deleteGare(gare);
            successResponse = ResponseFactory.seterResponse(
                    Constantes.Message.SUCCESS_BODY,
                    Constantes.Status.OK,
                    "La suppression de la gare : " + gare.getNom() + " a été un succès",
                    null
            );
            resultMap = new HashMap<>();
            resultMap.put("result", successResponse);
            responseDto.setSeter(resultMap);
        }
        catch (DataAccessException ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Erreur de base de données lors de la suppression de la gare avec l'ID " + gareId + " : " + ex.toString(),
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        catch (Exception ex)
        {
            errorResponse = ResponseFactory.seterResponse(
                    Constantes.Message.BAD_REQUEST_BODY,
                    Constantes.Status.BAD_REQUEST,
                    "Une erreur s'est produite lors de la suppression de la gare avec l'ID " + gareId + " : " + ex.toString(),
                    null
            );

            resultMap = new HashMap<>();
            resultMap.put("result", errorResponse);
            responseDto.setSeter(resultMap);
        }
        return responseDto;
    }
}
