package sn.setter.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sn.setter.models.Trajet

@Repository
interface TrajetRepository extends CrudRepository<Trajet,String> {

}