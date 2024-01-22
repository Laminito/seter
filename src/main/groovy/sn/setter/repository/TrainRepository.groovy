package sn.setter.repository

import org.springframework.data.repository.CrudRepository
import sn.setter.models.Train

interface TrainRepository extends CrudRepository<Train,String>{

}