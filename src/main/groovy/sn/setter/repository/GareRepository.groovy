package sn.setter.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import sn.setter.models.Gare

interface GareRepository extends CrudRepository<Gare,String>{
    @Query("SELECT gare FROM Gare gare WHERE gare.enabled = true")
    List<Gare> findAllActiveGares();
}