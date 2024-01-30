package sn.setter.services

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sn.setter.generic.SeterException
import sn.setter.models.Gare
import sn.setter.repository.GareRepository

@Service
@Slf4j
class GareService
{
    @Autowired
    private GareRepository garesRepository;

    public List<Gare> getAllGares() {
        try {
            log.info("La liste des gares a été retournée avec succès");
            return garesRepository.findAllActiveGares();
        } catch (SeterException ex) {
            log.error("La liste des gares est vide: " + ex.toString());
            return Collections.emptyList(); // Renvoie une liste vide en cas d'erreur
        }
        catch (Exception ex) {
            log.error("Une erreur est survenue lors de la récupération des gares : " + ex.toString());
            throw new SeterException("Une erreur est survenue lors de la récupération des gares :" + ex.toString())
        }
    }

    public addGare(Gare gare) {
        try {
            if (gare != null) {
                if (gare.nom == null) {
                    log.error("Le champ nom est requis");
                    throw new SeterException("Le champ nom est requis")
                }
                if (gare.adresse == null){
                    log.error("Le champ telephone est requis");
                    throw new SeterException("Le champ telephone est requis")
                }
                if (gare.latitude == 0){
                    log.error("Le champ latitude est requis");
                    throw new SeterException("Le champ latitude est requis")
                }
                if (gare.longitude == 0){
                    log.error("Le champ longitude est requis");
                    throw new SeterException("Le champ longitude est requis")
                }
                else {
                    log.info("Enregistrement reussie avec succes");
                    return garesRepository.save(gare);
                }
            } else {
                log.error("Echec lors de l'ajout d'une nouvelle gare");
                throw new SeterException("Le produit à ajouter est null.");
            }
        } catch (Exception ex) {
            log.error("Une erreur est survenue lors de l'ajout d'une nouvelle gare : {}", ex.toString());
            throw new SeterException("Erreur de validation lors de l'ajout d'une nouvelle gare"+ ex.toString());
        }
    }

    public getGareById(String id) {
         return garesRepository.findById(id).get()
    }

    public deleteGare(Gare gare) {
        garesRepository.save(gare);
    }
}
