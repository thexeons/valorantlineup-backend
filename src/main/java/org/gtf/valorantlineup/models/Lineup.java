package org.gtf.valorantlineup.models;

import lombok.Data;
import org.gtf.valorantlineup.enums.PSQLEnumType;
import org.gtf.valorantlineup.enums.Peta;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "lineups",
        indexes = {
        @Index(name = "uuid_index", columnList = "uuid")
        })
@TypeDef(
        name = "pgsql_enum",
        typeClass = PSQLEnumType.class
)
@Data
public class Lineup extends Base {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lineup",cascade = CascadeType.ALL) //mappedBy refers to the entity name on the other side
    private List<Node> nodes;

    @Column(name = "title")
    private String title;

    @Column(name = "map", columnDefinition = "map_info")
    @Enumerated(EnumType.STRING)
    @Type( type = "pgsql_enum" )
    private Peta map;

    @Column(name = "status")
    private String status;

}
