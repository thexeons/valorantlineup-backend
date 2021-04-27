package org.gtf.valorantlineup.models;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;

import java.util.UUID;

@MappedSuperclass //mapping information is applied to the entities that inherit from it
@DynamicUpdate //ensures that Hibernate uses only the modified columns in the SQL statement that it generates for the update of an entity.
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class Base {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    protected Long id;

    @Column(name = "uuid", unique = true, length = 36)
    private String uuid;

    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdDate", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime createdDate;

    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifiedDate", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime modifiedDate;

    @CreatedBy
    @Column(name = "createdBy")
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "ModifiedBy")
    private Long modifiedBy;

    public Base() {

    }

    @PrePersist
    public void createdDate() {
        this.uuid = UUID.randomUUID().toString();
    }

//    @PreUpdate
//    public void preUpdate(){
//    }

}
