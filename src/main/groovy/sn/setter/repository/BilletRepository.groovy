package sn.setter.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sn.setter.models.Billet

@Repository
interface BilletRepository extends CrudRepository<Billet,String> {

}