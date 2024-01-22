package sn.setter.models

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Getter
@Setter
@Table(name="roles")
@EntityListeners(AuditingEntityListener.class)
class Role {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    String id;
    String intitule;

    String userCreate = "Mohamed"
    String userUpdate = "Mohamed"

    boolean enabled = true

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date",nullable = false, updatable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    Date lastModifiedDate;

     Role(String intitule) {
        this.intitule = intitule;
    }

    Role() {

    }
}