package sn.setter.models

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import sn.setter.utils.Tools


@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) //Annotation pour autogenerer createdDate et lastModifiedDate lors de la creation et mise à jour d'un user
class Utilisateur {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    String id;

    String prenom;
    String nom;
    String email;
    String telephone;
    String username
    String password = Tools.encodePassword("passer123");

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Role> roles

    @OneToMany(mappedBy = "utilisateur")
    List<Horaires> horairesUtilises;

    @OneToMany(mappedBy = "utilisateur")
    List<Trajet> trajetsUtilises;

    @OneToMany(mappedBy = "utilisateur")
    List<Train> trainsUtilises;

    @ManyToMany
    @JoinTable(name = "utilisateur_gare",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "gare_id"))
    List<Gare> garesAccedees;

    @OneToMany(mappedBy = "utilisateur")
    List<Billet> billetsAchetes;

    String userCreate = "Mohamed";
    String userUpdate = "Mohamed";

    /*  Informations Informations Informations  */
    private boolean enabled = true
    private boolean accountExpired = false
    private boolean accountLocked = false
    private boolean passwordExpired = false
    private Boolean reinitPassword = false

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false, updatable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    Date lastModifiedDate;
}



