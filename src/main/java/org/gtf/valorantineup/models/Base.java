package org.gtf.valorantineup.models;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@DynamicUpdate
@Data
public abstract class Base {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    protected Long id;

    @Column(name = "uuid", unique = true, length = 36)
    private String uuid;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_CREATED", updatable = false)
	private Date creationDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED")
    private Date modificationDate;

    public Base() {

    }

    @PrePersist
    public void prePersist() {
        this.creationDate = new Date();
        this.uuid = UUID.randomUUID().toString();
    }

    @PreUpdate
    public void preUpdate(){
        this.modificationDate = new Date();
    }
}
