package sn.setter.repository

import org.springframework.data.repository.CrudRepository
import sn.setter.models.Horaires

interface HorairesRepository extends CrudRepository<Horaires,String>{
}
