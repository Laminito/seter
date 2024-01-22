package sn.setter.repository

import org.springframework.data.repository.CrudRepository
import sn.setter.models.Gare

interface GareRepository extends CrudRepository<Gare,String>{

}